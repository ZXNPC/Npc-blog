package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ArticleMapper;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Article;
import com.example.blognpc.provider.SearchProvider;
import com.example.blognpc.model.User;
import com.example.blognpc.utils.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftMapper draftMapper;
    @Autowired
    private SearchProvider searchProvider;

    public void createOrUpdate(Article article, Long draftId) {
        if (article.getId() == null) {
            // 创建文章
            article.setGmtCreate(System.currentTimeMillis());
            article.setGmtModified(article.getGmtCreate());
            article.setCommentCount(0);
            article.setViewCount(0);
            article.setLikeCount(0);
            articleMapper.insert(article);
            if (draftId != null)
                draftMapper.deleteById(draftId);
        } else {
            // 更新文章
            Article dbArticle = articleMapper.selectById(article.getId());
            if (dbArticle == null) {
                // 文章不存在
                throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
            } else {
                // 文章存在
                article.setGmtCreate(dbArticle.getGmtCreate());
                article.setGmtModified(System.currentTimeMillis());
                article.setCommentCount(dbArticle.getCommentCount());
                article.setViewCount(dbArticle.getViewCount());
                article.setLikeCount(dbArticle.getLikeCount());
                articleMapper.updateById(article);
            }
        }
    }

    public ArticleDTO selectById(Long id) {
        Article article = articleMapper.selectById(id);

        if (article == null)
            // 文章不存在
            throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);

        User user = userMapper.selectById(article.getCreator());

        if (user == null)
            // 创建文章的用户不存在，当然不应该出现这种情况
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);

        ArticleDTO articleDTO = new ArticleDTO();
        BeanUtils.copyProperties(article, articleDTO);
        articleDTO.setUser(user);
        return articleDTO;
    }

    public void incView(Long id) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("view_count = view_count+1");
        updateWrapper.eq("id", id);
        articleMapper.update(null, updateWrapper);
    }

    public void incCommet(Long id) {
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("comment_count = comment_count+1");
        updateWrapper.eq("id", id);
        articleMapper.update(null, updateWrapper);
    }

    public PaginationDTO<ArticleDTO> list(Long page, Long size, String orderDesc) {
        return list(null, page, size, null, orderDesc);
    }

    public PaginationDTO<ArticleDTO> list(Long creator, Long page, Long size, String orderDesc) {
        return list(creator, page, size, null, orderDesc);
    }

    public PaginationDTO<ArticleDTO> list(Long creator, Long page, Long size, String search, String orderDesc) {
        if (StringUtils.isBlank(search)) {
            // 不使用搜索
            Long totalCount = articleMapper.selectCount(null);
            PaginationDTO<ArticleDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size)));
            return getArticleDTOPaginationDTO(paginationDTO, articles);
        } else {
            // 使用搜索
            String regexp = searchProvider.generateRegexp(search, "title");

            QueryWrapper<Article> countWrapper = new QueryWrapper<Article>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp);

            Long totalCount = null;
            try {
                 totalCount = articleMapper.selectCount(countWrapper);
            } catch (BadSqlGrammarException e) {
                return null;
            }
            PaginationDTO<ArticleDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            QueryWrapper<Article> selectWrapper = new QueryWrapper<Article>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size));
            List<Article> articles = articleMapper.selectList(selectWrapper);
            return getArticleDTOPaginationDTO(paginationDTO, articles);
        }
    }

    private PaginationDTO<ArticleDTO> getArticleDTOPaginationDTO(PaginationDTO<ArticleDTO> paginationDTO, List<Article> articles) {
        Set<Long> creatorSet = articles.stream().map(article -> article.getCreator()).collect(Collectors.toSet());
        Map<Long, User> userMap = ServiceUtils.getUserMap(creatorSet);

        List<ArticleDTO> articleDTOS = articles.stream().map(article -> {
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            articleDTO.setUser(userMap.get(article.getCreator()));
            return articleDTO;
        }).collect(Collectors.toList());
        paginationDTO.setData(articleDTOS);

        return paginationDTO;
    }

    public List<ArticleDTO> selectRelated(ArticleDTO queryDTO, Long size) {
        String tags = queryDTO.getTag();
        String searchContent = Arrays.stream(tags.split(",")).map(tag -> {
            tag = tag.trim();
            return "(" + tag + ",)|(" + tag + "$)";
        }).collect(Collectors.joining("|"));
        String search = String.format("tag:\"%s\"", searchContent);

        return list(null, 0L, size, search, null).getData();
    }

    public List<ArticleDTO> selectRelated(QuestionDTO queryDTO, Long size) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setTag(queryDTO.getTag());
        return selectRelated(articleDTO, size);
    }
}
