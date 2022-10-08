package com.example.blognpc.mapper;

import com.example.blognpc.model.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author NPC
 * @since 2022-10-08
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
