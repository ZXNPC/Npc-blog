package com.example.blognpc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.dto.UserDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.model.Manager;
import com.example.blognpc.model.User;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.QuestionService;
import com.example.blognpc.service.UserService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ManageController {
    @Autowired
    private UserService userService;
    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private QuestionService questionService;
//    @Autowired
//    private ToolService toolService;

    @GetMapping("/manage")
    public String manage(Model model,
                         HttpServletRequest request,
                         @RequestParam(name = "page", defaultValue = "1") Long page,
                         @RequestParam(name = "size", defaultValue = "10") Long size,
                         @RequestParam(name = "search", required = false) String search) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        if (managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId())).size() == 0) {
            // 非管理员
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        model.addAttribute("search", search);
        return "manage";
    }

    @GetMapping("/manage/user")
    public String getUser(Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Long page,
                          @RequestParam(name = "size", defaultValue = "10") Long size,
                          @RequestParam(name = "search", required = false) String search) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        if (managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId())).size() == 0) {
            // 非管理员
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        PaginationDTO<UserDTO> paginationDTO = userService.list(page, size, search, null);
        model.addAttribute("paginationDTO", paginationDTO);

        return "manage-user";
    }

    @GetMapping("/manage/{section}")
    public ResultDTO manageSection(
            @PathVariable("section") String section,
            HttpServletRequest request
            ) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        if (managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId())).size() == 0) {
            // 非管理员
            return ResultDTO.errorOf(CustomizeErrorCode.NOT_MANAGER);
        }

        if ("article".equals(section)) {

        }
        return null;
    }

}
