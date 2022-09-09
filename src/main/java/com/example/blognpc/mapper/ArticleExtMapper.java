package com.example.blognpc.mapper;

import com.example.blognpc.model.Article;
import com.example.blognpc.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;
import java.util.List;

@Mapper
public interface ArticleExtMapper {
    @Update("update article set comment_count = comment_count + 1 where id = ${id}")
    void incComment(Serializable id);

    @Update("update article set view_count = view_count + 1 where id = ${id}")
    void incView(Serializable id);

    @Select("select * from article where ${column} regexp '${val}' limit ${size}")
    List<Article> selectRegexp(String column, Object val, Long size);

}
