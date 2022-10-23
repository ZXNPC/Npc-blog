package com.example.blognpc.controller;

import com.example.blognpc.cache.TagCache;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.Tool;
import com.example.blognpc.model.User;
import com.example.blognpc.service.ManagerService;
import com.example.blognpc.service.ToolService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DepotPublishController {
    @Autowired
    private ManagerService managerService;
    @Autowired
    private ToolService toolService;

    @GetMapping("/depot/publish")
    public String publish(Model model,
                          HttpServletRequest request,
                          @RequestParam(value = "id", required = false) Long id,
                          @RequestParam(value = "title", required = false) String title,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "tag", required = false) String tag,
                          @RequestParam(value = "draftId", required = false) Long draftId) {
        User user = (User) request.getSession().getAttribute("user");

        if (!managerService.isManager(user)) {
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        model.addAttribute("id", id);
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("draftId", draftId);
        model.addAttribute("tagDTOS", TagCache.get());
        return "depot-publish";
    }

    @PostMapping("/depot/publish")
    public String doPublish(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "tag") String tag,
            @RequestParam(value = "draftId", required = false) Long draftId,
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
            return "depot-publish";
        }


        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            model.addAttribute("resultDTO", ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN));
            return "depot-publish";
        }

        if (!managerService.isManager(user)) {
            throw new CustomizeException(CustomizeErrorCode.NOT_MANAGER);
        }

        Tool tool = new Tool();
        tool.setId(id);
        tool.setTitle(title);
        tool.setUrl(description);
        tool.setTag(tag);
        tool.setCreator(user.getId());

        toolService.createOrUpdate(tool, draftId);
        return "redirect:/depot";
    }
}
