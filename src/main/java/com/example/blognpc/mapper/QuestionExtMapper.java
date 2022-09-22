package com.example.blognpc.mapper;

import com.example.blognpc.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface QuestionExtMapper {
    void incComment(Serializable id);

    void incView(Serializable id);

    List<Question> selectRegexp(Long creator, String column, Object val, String orderColumn, Integer desc, Long offset, Long size);
    Long selectCountRegexp(Long creator, String column, Object val);

}
