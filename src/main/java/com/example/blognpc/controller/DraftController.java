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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DraftController {
    @Autowired
    private DraftService draftService;
    @GetMapping("/draft/{id}")
    public ModelAndView draft(@PathVariable("id") Long id,
                              RedirectAttributes attributes,
                              ModelMap model) {
        Draft draft = draftService.selectById(id);

        model.addAttribute("draftId", id);
        model.addAttribute("title", draft.getTitle());
        model.addAttribute("description", draft.getDescription());
        model.addAttribute("tag", draft.getTag());
        if (draft.getType() == DraftTypeEnum.QUESTION_DRAFT.getType()) {
            return new ModelAndView("redirect:/community/publish", model);
        } else if (draft.getType() == DraftTypeEnum.ARTICLE_DRAFT.getType()) {
            return new ModelAndView("redirect:/mumbler/publish", model);
        }
        return null;
//        attributes.addAttribute("draftId", id);
//        attributes.addAttribute("title", draft.getTitle());
//        attributes.addAttribute("description", draft.getDescription());
//        attributes.addAttribute("tag", draft.getTag());
//        redirectUri += String.format("?draftId=%d&title=%s&description=%s&tag=%s", id, draft.getTitle(), draft.getDescription(), draft.getTag());
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
