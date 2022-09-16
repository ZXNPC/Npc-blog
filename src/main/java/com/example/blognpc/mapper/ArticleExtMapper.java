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
    void incComment(Serializable id);

    void incView(Serializable id);

    List<Article> selectRegexp(Long creator, String column, Object val,Long offset, Long size);

    Long selectCountRegexp(Long creator, String column, Object val);

}
