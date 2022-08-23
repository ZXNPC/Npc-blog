package com.example.blognpc.mapper;

import com.example.blognpc.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface QuestionExtMapper {
//    @Select("select * from question")
//    List<Question> selectAll();
//
//    @Select("select * from question where id = ${id}")
//    List<Question> selectSome(Serializable id);
    @Update("update question set comment_count = comment_count + 1 where id = ${id}")
    void incComment(Serializable id);

    @Update("update question set view_count = view_count + 1 where id = ${id}")
    void incView(Serializable id);

    @Select("select * from question where ${column} regexp '${val}' limit ${size}")
    List<Question> selectRegexp(String column, Object val, Long size);



}
