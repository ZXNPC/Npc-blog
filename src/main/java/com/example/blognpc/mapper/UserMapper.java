package com.example.blognpc.mapper;

import com.example.blognpc.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author NPC
 * @since 2022-08-21
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
