package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.DraftDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.DraftTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.DraftExtMapper;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Draft;
import com.example.blognpc.model.Question;
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
public class DraftService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftMapper draftMapper;
    @Autowired
    private DraftExtMapper draftExtMapper;

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

        // TODO: 我想让 insert 直接返回该数据的主键 id ，暂时不知道如何实现，只好再次查询
        return draftMapper.selectById(draft).getId();
    }

    public PaginationDTO<DraftDTO> list(Long page, Long size) {
        return list(null, page, size, null);
    }

    public PaginationDTO<DraftDTO> list(Long creator, Long page, Long size) {
        return list(creator, page, size, null);
    }

    public PaginationDTO<DraftDTO> list(Long creator, Long page, Long size, String search) {
        Long totalCount;
        String titleRegexp;
        if (StringUtils.isBlank(search)) {
            titleRegexp = "";
            totalCount = draftMapper.selectCount(new QueryWrapper<Draft>().eq(creator != null && creator != 0L, "creator", creator));
        } else {
            titleRegexp = Arrays.stream(search.split(" ")).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining("|"));
            totalCount = draftExtMapper.selectCountRegexp(null, "title", titleRegexp);
        }
        PaginationDTO<DraftDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Draft> drafts;
        if (StringUtils.isBlank(titleRegexp)) {
            drafts = draftMapper.selectList(new QueryWrapper<Draft>()
                    .eq(creator != null && creator != 0L, "creator", creator)
                    .orderByDesc("gmt_create")
                    .last(String.format("limit %d, %d", offset, size)));
        } else {
            drafts = draftExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
        }

        // 生成 key = creator, value = User 的 Map
        List<Long> creatorList = drafts.stream().map(draft -> draft.getCreator()).collect(Collectors.toList());
        // 如果用户数量为空，则添加一个 0 ，防止 sql 语句错误
        if (creatorList.size() == 0) creatorList.add(0L);
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().in("id", creatorList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<DraftDTO> draftDTOS = new ArrayList<DraftDTO>();
        for (Draft draft : drafts) {
            User user = userMap.get(draft.getCreator());
            if (user == null) {
                // 数据库会用外键联系用户和问题创建人，所有是不会存在找不到用户的情况的，但以防万一先抛个异常
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            }
            DraftDTO draftDTO = new DraftDTO();
            BeanUtils.copyProperties(draft, draftDTO);
            draftDTO.setUser(user);
            draftDTOS.add(draftDTO);
        }
        paginationDTO.setData(draftDTOS);
        return paginationDTO;
    }

    public Long countById(Long creator) {
        return draftMapper.selectCount(new QueryWrapper<Draft>().eq("creator", creator));
    }

    public void deleteById(Long id, User user) {
        Draft draft = draftMapper.selectById(id);
        if (draft == null) {
            throw new CustomizeException(CustomizeErrorCode.DRAFT_NOT_FOUND);
        }

        if (draft.getCreator() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.ACCOUNT_ERROR);
        }

        draftMapper.deleteById(id);
        return;
    }
}
