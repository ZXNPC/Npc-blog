package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ArticleExtMapper;
import com.example.blognpc.mapper.ArticleMapper;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Article;
import com.example.blognpc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleExtMapper articleExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftMapper draftMapper;

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
        articleExtMapper.incView(id);
    }

    public PaginationDTO<ArticleDTO> list(Long page, Long size) {
        return list(null, page, size, null);
    }

    public PaginationDTO<ArticleDTO> list(Long creator, Long page, Long size) {
        return list(creator, page, size, null);
    }

    public PaginationDTO<ArticleDTO> list(Long creator, Long page, Long size, String search) {
        Long totalCount;
        String titleRegexp;
        if (StringUtils.isBlank(search)) {
            titleRegexp = "";
            totalCount = articleMapper.selectCount(new QueryWrapper<Article>().eq(creator != null && creator != 0L, "creator", creator));
        } else {
            titleRegexp = Arrays.stream(search.split(" ")).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining("|"));
            totalCount = articleExtMapper.selectCountRegexp(null, "title", titleRegexp);
        }
        PaginationDTO<ArticleDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Article> articles;
        if (StringUtils.isBlank(titleRegexp)) {
            articles = articleMapper.selectList(new QueryWrapper<Article>()
                    .eq(creator != 0L, "creator", creator)
                    .orderByDesc("gmt_create")
                    .last(String.format("limit %d, %d", offset, size)));
        } else {
            articles = articleExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
        }

        // 生成 key = creator, value = User 的 Map
        List<Long> creatorList = articles.stream().map(article -> article.getCreator()).collect(Collectors.toList());
        // 如果用户数量为空，则添加一个 0 ，防止 sql 语句错误
        if (creatorList.size() == 0) creatorList.add(0L);
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().in(!creatorList.isEmpty(), "id", creatorList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<ArticleDTO> articleDTOS = new ArrayList<ArticleDTO>();
        for (Article article : articles) {
            User user = userMap.get(article.getCreator());
            if (user == null) {
                // 数据库会用外键联系用户和问题创建人，所有是不会存在找不到用户的情况的，但以防万一先抛个异常
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            }
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            articleDTO.setUser(user);
            articleDTOS.add(articleDTO);
        }
        paginationDTO.setData(articleDTOS);
        return paginationDTO;
    }

    public List<ArticleDTO> selectRelated(ArticleDTO queryDTO, Long size) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            // 正常前端和数据库都会限制该情况的出现，所以我直接抛异常了
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
        }

        return selectRelated(queryDTO.getId(), queryDTO.getTag(), size);
    }

    public List<ArticleDTO> selectRelated(QuestionDTO queryDTO, Long size) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            // 正常前端和数据库都会限制该情况的出现，所以我直接抛异常了
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
        }

        return selectRelated(0L, queryDTO.getTag(), size);
    }

    public List<ArticleDTO> selectRelated(Long id, String tags, Long size) {
        // id 是用来过滤问题的，防止相关问题搜寻到自己，而文章的相关问题则不需考虑重复，故设置为0
        String regexp = Arrays.stream(tags.split(",")).map(tag -> {
            tag = tag.trim();
            return "(" + tag + ",)|(" + tag + "$)";
        }).collect(Collectors.joining("|"));
        List<Article> articles = articleExtMapper.selectRegexp(null, "tag", regexp, "gmt_create", 1, 0L, size);
        List<ArticleDTO> articleDTOS = articles.stream().filter(article -> !(article.getId() == id)).map(article -> {
            ArticleDTO articleDTO = new ArticleDTO();
            BeanUtils.copyProperties(article, articleDTO);
            // 这里并没有把 user信息 放到ArticleDTO之中，注意一下
            return articleDTO;
        }).collect(Collectors.toList());
        return articleDTOS;
    }
}
