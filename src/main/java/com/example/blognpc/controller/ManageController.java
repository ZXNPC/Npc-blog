package com.example.blognpc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.cache.TagCache;
import com.example.blognpc.dto.*;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.model.Annotation;
import com.example.blognpc.model.Manager;
import com.example.blognpc.model.Tool;
import com.example.blognpc.model.User;
import com.example.blognpc.service.*;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ManageController {
    @Autowired
    private UserService userService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private AnnotationService annotationService;

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

        if (!managerService.isManager(user)) {
            // 非管理员
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        model.addAttribute("search", search);
        return "manage";
    }

//    TODO: 添加用户管理员
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

        if (!managerService.isManager(user)) {
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
    public ModelAndView modify(
            HttpServletRequest request,
            ModelMap model,
            @RequestParam("id") Long id,
            @RequestParam(value = "annoId", required = false) Long annoId,
            @RequestParam(value = "draftId", required = false) Long draftId,
            @RequestParam(value = "description", required = false) String description,
            @PathVariable("section") String section
    ) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        if (!managerService.isManager(user)) {
            // 非管理员
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        if ("article".equals(section)) {
            ArticleDTO articleDTO = articleService.selectById(id);
            ModelAndView modelAndView = new ModelAndView("manage-article");
            modelAndView.addObject("articleDTO", articleDTO);
            modelAndView.addObject("draftId", draftId);
            modelAndView.addObject("description", description);
            if (annoId != null)
                modelAndView.addObject("description", annotationService.selectById(annoId).getDescription());
            return modelAndView;
        } else if ("question".equals(section)) {
            QuestionDTO questionDTO = questionService.selectById(id);
            ModelAndView modelAndView = new ModelAndView("manage-question");
            modelAndView.addObject("questionDTO", questionDTO);
            modelAndView.addObject("draftId", draftId);
            modelAndView.addObject("description", description);
            if (annoId != null)
                modelAndView.addObject("description", annotationService.selectById(annoId).getDescription());
            return modelAndView;
        } else if ("tool".equals(section)) {
            ToolDTO toolDTO = toolService.selectById(id);
            model.addAttribute("id", toolDTO.getId());
            model.addAttribute("title", toolDTO.getTitle());
            model.addAttribute("description", toolDTO.getUrl());
            model.addAttribute("tag", toolDTO.getTag());
            model.addAttribute("tagDTOS", TagCache.get());
            return new ModelAndView("redirect:/depot/publish", model);
        }
        return null;
    }

    @ResponseBody
    @PostMapping("/manage/{section}/modify")
    public String anno() {
        return null;
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

        if (!managerService.isManager(user)) {
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
