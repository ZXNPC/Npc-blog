package com.example.blognpc.controller;

import com.example.blognpc.cache.TagCache;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.Article;
import com.example.blognpc.model.User;
import com.example.blognpc.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MumblerPublishController {
    @Autowired
    private ArticleService articleService;

    @Value("${blog.manager.token}")
    private String managerToken;

    @GetMapping("/mumbler/publish")
    public String publish(Model model,
                          @RequestParam(value = "id", required = false) Long id,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "tag", required = false) String tag,
                          @RequestParam(value = "draftId", required = false) Long draftId) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("draftId", draftId);
        model.addAttribute("tagDTOS", TagCache.get());
        return "mumbler-publish";
    }

    @GetMapping("/mumbler/publish/{id}")
    public String edit(@PathVariable("id") Long id,
                       Model model) {
        ArticleDTO articleDTO = articleService.selectById(id);
        model.addAttribute("title", articleDTO.getTitle());
        model.addAttribute("description", articleDTO.getDescription());
        model.addAttribute("tag", articleDTO.getTag());
        model.addAttribute("tagDTOS", TagCache.get());
        return "mumbler-publish";
    }

    @PostMapping("/mumbler/publish")
    public String doPublish(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "tag") String tag,
            @RequestParam(value = "draftId") Long draftId,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("draftId", draftId);
        model.addAttribute("tagDTOS", TagCache.get());

        if (StringUtils.isBlank(title) || StringUtils.isBlank(description) || StringUtils.isBlank(tag)) {
            throw new CustomizeException(CustomizeErrorCode.FRONT_END_ERROR);
        }

        String cs = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(cs)) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.TAG_ERROR));
            return "mumbler-publish";
        }


        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN));
            return "mumbler-publish";
        }

        if (!user.getToken().equals(managerToken)) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.NOT_MANAGER));
            return "redirect:/";
        }

//        Question question = new Question();
//        question.setId(id);
//        question.setTitle(title);
//        question.setDescription(description);
//        question.setTag(tag);
//        question.setCreator(user.getId());
//
//        questionService.createOrUpdate(question);
//        return "redirect:/mumbler";

        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setDescription(description);
        article.setTag(tag);
        article.setCreator(user.getId());

        articleService.createOrUpdate(article, draftId);
        return "redirect:/mumbler";

    }
}
