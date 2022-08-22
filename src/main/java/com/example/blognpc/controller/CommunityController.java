package com.example.blognpc.controller;

import com.example.blognpc.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommunityController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/community")
    public String community(Model model,
                            @RequestParam(name = "page", defaultValue = "1") Long page,
                            @RequestParam(name = "size", defaultValue = "5") Long size) {
        questionService.list(page, size);
        return "community";
    }
}
