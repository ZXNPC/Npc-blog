package com.example.blognpc.controller;


import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.CommentService;
import com.example.blognpc.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author NPC
 * @since 2022-08-23
 */
@Controller
public class MumblerArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private QuestionService questionService;

    @GetMapping("/mumbler/article/{id}")
    public String article(@PathVariable(name = "id") Long id,
                           @RequestParam(name = "size", defaultValue = "20") Long size,
                           Model model) {
        ArticleDTO articleDTO = articleService.selectById(id);
        List<ArticleDTO> relatedArticles = articleService.selectRelated(articleDTO, size);    // 这里并没有把 user信息 放到ArticleDTO之中，注意一下
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.MUMBLER_ARTICLE);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(articleDTO, size);

        articleService.incView(id);
        model.addAttribute("articleDTO", articleDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("relatedArticles", relatedArticles);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "mumbler-article";
    }
}
