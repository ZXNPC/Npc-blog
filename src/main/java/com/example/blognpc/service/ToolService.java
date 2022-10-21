package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.dto.ToolDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.NotificationTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ToolMapper;
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
public class ToolService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftService draftService;
    @Autowired
    private ToolMapper toolMapper;
    @Autowired
    private SearchProvider searchProvider;
    @Autowired
    private NotificationService notificationService;

    public void incView(Long id) {
        UpdateWrapper<Tool> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("view_count = view_count+1");
        updateWrapper.eq("id", id);
        toolMapper.update(null, updateWrapper);
    }

    public PaginationDTO<ToolDTO> list(Long page, Long size, String orderDesc) {
        return list(null, page, size, null, orderDesc);
    }

    public PaginationDTO<ToolDTO> list(Long creator, Long page, Long size, String orderDesc) {
        return list(creator, page, size, null, orderDesc);
    }

    /**
     * @param creator   创建人
     * @param page      页
     * @param size      每页包含的数量
     * @param search    搜索内容 e.g. title:java
     * @param orderDesc 倒序依据
     * @return
     */
    public PaginationDTO<ToolDTO> list(Long creator, Long page, Long size, String search, String orderDesc) {
        if (StringUtils.isBlank(search)) {
            // 不使用搜索
            Long totalCount = toolMapper.selectCount(null);
            PaginationDTO<ToolDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<Tool> tools = toolMapper.selectList(new QueryWrapper<Tool>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size)));
            return getToolDTOPaginationDTO(paginationDTO, tools);
        } else {
            // 使用搜索
            String regexp = searchProvider.generateRegexp(search, "title");

            QueryWrapper<Tool> countWrapper = new QueryWrapper<Tool>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp);

            Long totalCount = null;
            try {
                totalCount = toolMapper.selectCount(countWrapper);
            } catch (BadSqlGrammarException e) {
                return null;
            }
            PaginationDTO<ToolDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            QueryWrapper<Tool> selectWrapper = new QueryWrapper<Tool>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size));
            List<Tool> tools = toolMapper.selectList(selectWrapper);
            return getToolDTOPaginationDTO(paginationDTO, tools);
        }
    }

    private PaginationDTO<ToolDTO> getToolDTOPaginationDTO(PaginationDTO<ToolDTO> paginationDTO, List<Tool> tools) {
        Set<Long> creatorSet = tools.stream().map(tool -> tool.getCreator()).collect(Collectors.toSet());
        Map<Long, User> userMap = ServiceUtils.getUserMap(creatorSet);

        List<ToolDTO> toolDTOS = tools.stream().map(tool -> {
            ToolDTO toolDTO = new ToolDTO();
            BeanUtils.copyProperties(tool, toolDTO);
            toolDTO.setUser(userMap.get(toolDTO.getCreator()));
            return toolDTO;
        }).collect(Collectors.toList());
        paginationDTO.setData(toolDTOS);

        return paginationDTO;
    }

    public ResultDTO deleteById(Long id, User user) {
        try {
            Tool tool = toolMapper.selectById(id);
            if (tool == null) {
                throw new CustomizeException(CustomizeErrorCode.TOOL_NOT_FOUND);
            }
            Long draftId = draftService.createFromItem(tool);
            toolMapper.deleteById(id);
            notificationService.create(user.getId(), tool.getCreator(), draftId, NotificationTypeEnum.MANAGER_DELETE_TOOL.getType());
        } catch (Exception e) {
            return ResultDTO.errorOf(e);
        }
        return ResultDTO.okOf();
    }
}
