package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.DraftDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.DraftTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.*;
import com.example.blognpc.provider.SearchProvider;
import com.example.blognpc.utils.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DraftService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftMapper draftMapper;
    @Autowired
    private SearchProvider searchProvider;

    public DraftDTO toDraftDTO(Draft draft, User user) {
        DraftDTO draftDTO = new DraftDTO();
        BeanUtils.copyProperties(draft, draftDTO);
        draftDTO.setUser(user);
        return draftDTO;
    }

    public DraftDTO toDraftDTO(Draft draft, Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.USER_NOT_FOUND);
        }
        return toDraftDTO(draft, user);
    }

    public Draft selectById(Long id) {
        Draft draft = draftMapper.selectById(id);
        if (draft == null) {
            throw new CustomizeException(CustomizeErrorCode.DRAFT_NOT_FOUND);
        }

        if (!DraftTypeEnum.isExist(draft.getType())) {
            throw new CustomizeException(CustomizeErrorCode.DRAFT_TYPE_WRONG);
        }

        return draft;
    }

    public Long createOrUpdate(Draft draft) {
        if (draft.getType() == null || !DraftTypeEnum.isExist(draft.getType())) {
            throw new CustomizeException(CustomizeErrorCode.DRAFT_TYPE_WRONG);
        }

        if (draft.getId() == null) {
            // 创建草稿
            draft.setGmtCreate(System.currentTimeMillis());
            draft.setGmtModified(draft.getGmtCreate());
            draftMapper.insert(draft);
        } else {
            // 更新草稿
            Draft dbDraft = draftMapper.selectById(draft);
            if (dbDraft == null) {
                // 草稿不存在
                throw new CustomizeException(CustomizeErrorCode.DRAFT_NOT_FOUND);
            }
            draft.setGmtCreate(dbDraft.getGmtCreate());
            draft.setGmtModified(System.currentTimeMillis());
            draftMapper.updateById(draft);
        }

        return draftMapper.selectById(draft).getId();
    }

    public PaginationDTO<DraftDTO> list(Long page, Long size, String orderDesc) {
        return list(null, page, size, null, orderDesc);
    }

    public PaginationDTO<DraftDTO> list(Long creator, Long page, Long size, String orderDesc) {
        return list(creator, page, size, null, orderDesc);
    }

    public PaginationDTO<DraftDTO> list(Long creator, Long page, Long size, String search, String orderDesc) {
        if (StringUtils.isBlank(search)) {
            // 不使用搜索
            Long totalCount = draftMapper.selectCount(new QueryWrapper<Draft>().eq(creator != null && creator != 0L, "creator", creator));
            PaginationDTO<DraftDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<Draft> drafts = draftMapper.selectList(new QueryWrapper<Draft>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size)));
            return getDraftDTOPaginationDTO(paginationDTO, drafts);
        } else {
            // 使用搜索
            String regexp = searchProvider.generateRegexp(search, "title");

            QueryWrapper<Draft> countWrapper = new QueryWrapper<Draft>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp);

            Long totalCount = null;
            try {
                totalCount = draftMapper.selectCount(countWrapper);
            } catch (BadSqlGrammarException e) {
                return null;
            }
            PaginationDTO<DraftDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            QueryWrapper<Draft> selectWrapper = new QueryWrapper<Draft>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size));
            List<Draft> drafts = draftMapper.selectList(selectWrapper);
            return getDraftDTOPaginationDTO(paginationDTO, drafts);
        }
    }

    private PaginationDTO<DraftDTO> getDraftDTOPaginationDTO(PaginationDTO<DraftDTO> paginationDTO, List<Draft> drafts) {
        Set<Long> creatorList = drafts.stream().map(draft -> draft.getCreator()).collect(Collectors.toSet());
        Map<Long, User> userMap = ServiceUtils.getUserMap(creatorList);

        List<DraftDTO> draftDTOS = drafts.stream().map(draft -> {
            DraftDTO draftDTO = toDraftDTO(draft, userMap.get(draft.getCreator()));
            return draftDTO;
        }).collect(Collectors.toList());
        paginationDTO.setData(draftDTOS);

        return paginationDTO;
    }

    public Long countById(Long creator) {
        return draftMapper.selectCount(new QueryWrapper<Draft>().eq("creator", creator));
    }

    public ResultDTO deleteById(Long id, User user) {
        Draft draft = draftMapper.selectById(id);
        if (draft == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.DRAFT_NOT_FOUND);
        }

        if (draft.getCreator() != user.getId()) {
            return ResultDTO.errorOf(CustomizeErrorCode.ACCOUNT_ERROR);
        }

        draftMapper.deleteById(id);
        return ResultDTO.okOf();
    }

    public Long createFromItem(Question question) {
        return createFromItem(question.getCreator(), question.getTitle(), question.getDescription(), question.getTag(), DraftTypeEnum.QUESTION_DRAFT.getType());
    }

    public Long createFromItem(Article article) {
        return createFromItem(article.getCreator(), article.getTitle(), article.getDescription(), article.getTag(), DraftTypeEnum.ARTICLE_DRAFT.getType());
    }

    public Long createFromItem(Tool tool) {
        return createFromItem(tool.getCreator(), tool.getTitle(), tool.getUrl(), tool.getTag(), DraftTypeEnum.TOOL_DRAFT.getType());
    }

    private Long createFromItem(Long creator, String title, String description, String tag, Integer type) {

        Draft draft = new Draft();
        draft.setType(type);
        draft.setTitle(title);
        draft.setDescription(description);
        draft.setTag(tag);
        draft.setCreator(creator);
        return createOrUpdate(draft);
    }
}
