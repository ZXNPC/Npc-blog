package com.example.blognpc.controller;

import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.model.Question;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Long page,
                        @RequestParam(name = "size", defaultValue = "5") Long size) {
        PaginationDTO<ArticleDTO> paginationDTO = articleService.list(0L, page, size);
        model.addAttribute("paginationDTO", paginationDTO);
        return "index";
    }
}
