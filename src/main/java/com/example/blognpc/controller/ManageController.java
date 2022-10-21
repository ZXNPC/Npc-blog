package com.example.blognpc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.*;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.model.Manager;
import com.example.blognpc.model.User;
import com.example.blognpc.service.ArticleService;
import com.example.blognpc.service.QuestionService;
import com.example.blognpc.service.ToolService;
import com.example.blognpc.service.UserService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @Autowired
    private ToolService toolService;

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

//    @GetMapping("/manage/user")
//    public String getUser(Model model,
//                          HttpServletRequest request,
//                          @RequestParam(name = "page", defaultValue = "1") Long page,
//                          @RequestParam(name = "size", defaultValue = "20") Long size,
//                          @RequestParam(name = "search", required = false) String search) {
//        User user = (User) request.getSession().getAttribute("user");
//        if (user == null) {
//            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
//        }
//
//        if (managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId())).size() == 0) {
//            // 非管理员
//            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
//        }
//
//        PaginationDTO<UserDTO> paginationDTO = userService.list(page, size, search, null);
//        model.addAttribute("paginationDTO", paginationDTO);
//
//        return "manage-user";
//    }

    @ResponseBody
    @GetMapping("/manage/{section}")
    public ResultDTO manageSection(
            @PathVariable("section") String section,
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "size", defaultValue = "20") Long size,
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
            PaginationDTO<ArticleDTO> paginationDTO = articleService.list(page, size, "gmt_create");
            return ResultDTO.okOf(paginationDTO);
        } else if ("question".equals(section)) {
            PaginationDTO<QuestionDTO> paginationDTO = questionService.list(page, size, "gmt_create");
            return ResultDTO.okOf(paginationDTO);
        } else if ("tool".equals(section)) {
            PaginationDTO<ToolDTO> paginationDTO = toolService.list(page, size, "gmt_create");
            return ResultDTO.okOf(paginationDTO);
        }
        return null;
    }

    @ResponseBody
    @GetMapping("/manage/{section}/modify")
    public String modify(
            @RequestParam("id") Long id,
            @PathVariable("section") String section
    ) {
        return "temp";
    }

    @ResponseBody
    @PostMapping("/manage/{section}/delete")
    public ResultDTO delete(
            @RequestParam("id") Long id,
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

        ResultDTO resultDTO;
        if ("article".equals(section)) {
             resultDTO = articleService.deleteById(id, user);
        } else if ("question".equals(section)) {
            resultDTO = questionService.deleteById(id, user);
        } else if ("tool".equals(section)) {
            resultDTO = toolService.deleteById(id, user);
        } else {
            resultDTO = new ResultDTO();
        }

        return resultDTO;
    }
}
