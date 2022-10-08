package com.example.blognpc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.Tool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface ToolExtMapper {
    void incView(Serializable id);

    List<Tool> selectRegexp(Long creator, String column, Object val, String orderColumn, Integer desc, Long offset, Long size);

    Long selectCountRegexp(Long creator, String column, Object val);
}
