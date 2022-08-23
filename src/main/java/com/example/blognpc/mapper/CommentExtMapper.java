package com.example.blognpc.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.io.Serializable;

@Mapper
public interface CommentExtMapper {
    @Update("update comment set comment_count = comment_count + 1 where id = ${id}")
    void incComment(Serializable id);
}
