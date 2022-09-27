package com.example.blognpc.mapper;

import com.example.blognpc.model.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author NPC
 * @since 2022-09-27
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

}
