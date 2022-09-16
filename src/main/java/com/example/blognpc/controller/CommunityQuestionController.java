package com.example.blognpc.controller;


import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.CommentService;
import com.example.blognpc.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

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
public class CommunityQuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ArticleService articleService;

    @GetMapping("/community/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           @RequestParam(name = "size", defaultValue = "20") Long size,
                           Model model) {
        QuestionDTO questionDTO = questionService.selectById(id);
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMUNITY_QUESTION);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO, size);
        List<ArticleDTO> relatedArticles = articleService.selectRelated(questionDTO, size);

        questionService.incView(id);
        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        model.addAttribute("relatedArticles", relatedArticles);
        return "community-question";
    }
}
