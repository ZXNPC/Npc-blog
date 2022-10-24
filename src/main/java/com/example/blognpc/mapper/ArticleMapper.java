package com.example.blognpc.mapper;

import com.example.blognpc.model.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author NPC
 * @since 2022-10-24
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
