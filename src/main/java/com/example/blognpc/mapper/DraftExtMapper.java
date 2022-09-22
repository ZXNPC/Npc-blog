package com.example.blognpc.mapper;

import com.example.blognpc.model.Article;
import com.example.blognpc.model.Draft;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DraftExtMapper {
    List<Draft> selectRegexp(Long creator, String column, Object val, String orderColumn, Integer desc, Long offset, Long size);;

    Long selectCountRegexp(Long creator, String column, Object val);
}
