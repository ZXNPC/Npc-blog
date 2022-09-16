package com.example.blognpc.controller;

import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MumblerController {
    @Autowired
    private ArticleService articleService;

    @GetMapping("/mumbler")
    public String mumbler(Model model,
                          @RequestParam(name = "page", defaultValue = "1") Long page,
                          @RequestParam(name = "size", defaultValue = "10") Long size,
                          @RequestParam(name = "search", required = false) String search) {
        PaginationDTO<ArticleDTO> paginationDTO = articleService.list(0L, page, size, search);
        model.addAttribute("paginationDTO", paginationDTO);
        model.addAttribute("search", search);
        return "mumbler";
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Long page,
                        @RequestParam(name = "size", defaultValue = "10") Long size,
                        @RequestParam(name = "search", required = false) String search) {
        PaginationDTO<ArticleDTO> paginationDTO = articleService.list(0L, page, size, search);
        model.addAttribute("paginationDTO", paginationDTO);
        model.addAttribute("search", search);
        return "mumbler";
    }
}
