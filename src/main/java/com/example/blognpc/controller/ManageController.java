package com.example.blognpc.controller;

import com.example.blognpc.cache.TagCache;
import com.example.blognpc.dto.*;
import com.example.blognpc.enums.AnnotationTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.Annotation;
import com.example.blognpc.model.User;
import com.example.blognpc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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

    @PostMapping("/manage/{section}")
    public String addAnnotaion(
            HttpServletRequest request,
            @PathVariable("section") String section,
            @RequestParam("id") Long outerId,
            @RequestParam(value = "draftId", required = false) Long draftId,
            @RequestParam(value = "annoId", required = false) Long annoId,
            @RequestParam("description") String description
    ) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_LOGIN);
        }

        if (!managerService.isManager(user)) {
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        Annotation annotation = new Annotation();
        annotation.setId(annoId);
        annotation.setCreator(user.getId());
        annotation.setDescription(description);
        annotation.setOuterId(outerId);

        if ("article".equals(section)) {
            annotation.setType(AnnotationTypeEnum.ARTICLE_ANNO.getType());
            annotationService.createOrUpdate(annotation, draftId);
            return "redirect:/mumbler/article/" + outerId;
        } else if ("question".equals(section)) {
            annotation.setType(AnnotationTypeEnum.QUESTION_ANNO.getType());
            annotationService.createOrUpdate(annotation, draftId);
            return "redirect:/community/question/" + outerId;
        }

        return null;
    }

    @ResponseBody
    @GetMapping("/manage/{section}/modify")
    public ModelAndView modify(
            HttpServletRequest request,
            ModelMap model,
            @RequestParam("id") Long outerId,
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
            ArticleDTO articleDTO = articleService.selectById(outerId);
            AnnotationDTO annotationDTO = annotationService.selectByOuterId(outerId);

            ModelAndView modelAndView = new ModelAndView("manage-article");
            modelAndView.addObject("questionDTO", articleDTO);
            modelAndView.addObject("draftId", draftId);
            modelAndView.addObject("description", description);
            if (annotationDTO != null) {
                modelAndView.addObject("description", annotationDTO.getDescription());
            }
            return modelAndView;
        } else if ("question".equals(section)) {
            QuestionDTO questionDTO = questionService.selectById(outerId);
            AnnotationDTO annotationDTO = annotationService.selectByOuterId(outerId);

            ModelAndView modelAndView = new ModelAndView("manage-question");
            modelAndView.addObject("questionDTO", questionDTO);
            modelAndView.addObject("draftId", draftId);
            modelAndView.addObject("description", description);
            if (annotationDTO != null) {
                modelAndView.addObject("description", annotationDTO.getDescription());
            }
            return modelAndView;
        } else if ("tool".equals(section)) {
            ToolDTO toolDTO = toolService.selectById(outerId);
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
