package com.example.blognpc.controller;


import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CommentTypeEnum;
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
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           @RequestParam(name = "size", defaultValue = "20") Long size,
                           Model model) {
        QuestionDTO questionDTO = questionService.selectById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO, size);    // 这里并没有把 user信息 放到questionDTO之中，注意一下
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        questionService.incView(id);
        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("commentDTOS", commentDTOS);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
}
