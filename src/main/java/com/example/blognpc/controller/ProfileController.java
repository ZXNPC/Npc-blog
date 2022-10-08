package com.example.blognpc.controller;

import com.example.blognpc.dto.*;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.User;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.DraftService;
import com.example.blognpc.service.NotificationService;
import com.example.blognpc.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DraftService draftService;

    @Value("${blog.manager.token}")
    private String managerToken;

    @GetMapping("/profile/{section}")
    public String profile(@PathVariable(name = "section") String section,
                          HttpServletRequest request,
                          Model model,
                          @RequestParam(name = "searcg", required = false) String search,
                          @RequestParam(name = "page", defaultValue = "1") Long page,
                          @RequestParam(name = "size", defaultValue = "10") Long size) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        Long draftCount = draftService.countById(user.getId());
        model.addAttribute("draftCount", draftCount);

        if ("questions".equals(section)) {
            // 我的问题
            PaginationDTO<QuestionDTO> paginationDTO = questionService.list(user.getId(), page, size, search, "gmt_create");
            model.addAttribute("paginationDTO", paginationDTO);
        } else if ("articles".equals(section)) {
            // 我的文章
            if (!user.getToken().equals(managerToken)) {
                throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
            }
            PaginationDTO<ArticleDTO> paginationDTO = articleService.list(user.getId(), page, size, "gmt_create");
            model.addAttribute("paginationDTO", paginationDTO);
        } else if ("replies".equals(section)) {
            // 最新回复
            PaginationDTO<NotificationDTO> paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("paginationDTO", paginationDTO);
        }
        else if ("drafts".equals(section)) {
            // 我的草稿
            PaginationDTO<DraftDTO> paginationDTO = draftService.list(user.getId(), page, size, search, "gmt_create");
            model.addAttribute("paginationDTO", paginationDTO);
        }
        else {
        }
        return "profile-" + section;
    }
}
