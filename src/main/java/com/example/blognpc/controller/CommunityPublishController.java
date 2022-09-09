package com.example.blognpc.controller;

import com.example.blognpc.cache.TagCache;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
import com.example.blognpc.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommunityPublishController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/community/publish")
    public String publish(Model model) {
        model.addAttribute("tagDTOS", TagCache.get());
        return "community-publish";
    }

    @GetMapping("/community/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model) {
        QuestionDTO questionDTO = questionService.selectById(id);
        model.addAttribute("title", questionDTO.getTitle());
        model.addAttribute("description", questionDTO.getDescription());
        model.addAttribute("tag", questionDTO.getTag());
        model.addAttribute("tagDTOS", TagCache.get());
        return "community-publish";
    }

    @PostMapping("/community/publish")
    public String doPublish(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "tag") String tag,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tagDTOS", TagCache.get());

        if (StringUtils.isBlank(title) || StringUtils.isBlank(description) || StringUtils.isBlank(tag)) {
            throw new CustomizeException(CustomizeErrorCode.FRONT_ERROR);
        }

        String cs = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(cs)) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.TAG_ERROR));
            return "community-publish";
        }


        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN));
            return "community-publish";
        }

        Question question = new Question();
        question.setId(id);
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());

        questionService.createOrUpdate(question);
        return "redirect:/community";
    }
}
