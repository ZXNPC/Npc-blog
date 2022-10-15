package com.example.blognpc.controller;

import com.example.blognpc.dto.DraftCreateDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.DraftTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.Draft;
import com.example.blognpc.model.User;
import com.example.blognpc.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DraftController {
    @Autowired
    private DraftService draftService;
    // TODO: 写草稿时内容过长会报错
    @GetMapping("/draft/{id}")
    public String draft(@PathVariable("id") Long id,
                        RedirectAttributes attributes) {
        Draft draft = draftService.selectById(id);
        String redirectUri = "";
        if (draft.getType() == DraftTypeEnum.QUESTION_DRAFT.getType()) {
            redirectUri = "/community/publish";
        } else if (draft.getType() == DraftTypeEnum.ARTICLE_DRAFT.getType()) {
            redirectUri = "/mumbler/publish";
        }
        attributes.addAttribute("draftId", id);
        attributes.addAttribute("title", draft.getTitle());
        attributes.addAttribute("description", draft.getDescription());
        attributes.addAttribute("tag", draft.getTag());
//        redirectUri += String.format("?draftId=%d&title=%s&description=%s&tag=%s", id, draft.getTitle(), draft.getDescription(), draft.getTag());
        return "redirect:" + redirectUri;
    }

    @ResponseBody
    @PostMapping("/draft")
    public ResultDTO<Long> save(@RequestBody DraftCreateDTO draftCreateDTO,
                                HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        Draft draft = new Draft();
        draft.setId(draftCreateDTO.getId());
        draft.setType(draftCreateDTO.getType());
        draft.setTitle(draftCreateDTO.getTitle());
        draft.setDescription(draftCreateDTO.getDescription());
        draft.setTag(draftCreateDTO.getTag());
        draft.setCreator(user.getId());

        Long id = draftService.createOrUpdate(draft);

        return ResultDTO.okOf(id);
    }

    @ResponseBody
    @PostMapping("/draft/delete")
    public ResultDTO delete(@RequestBody Long id,
                            HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        ResultDTO resultDTO = draftService.deleteById(id, user);

        return resultDTO;
    }
}
