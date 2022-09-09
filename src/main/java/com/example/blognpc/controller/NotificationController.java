package com.example.blognpc.controller;

import com.example.blognpc.dto.NotificationDTO;
import com.example.blognpc.enums.NotificationTypeEnum;
import com.example.blognpc.model.User;
import com.example.blognpc.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String notification(@PathVariable(name = "id") Long id,
                               HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id, user);
        if (NotificationTypeEnum.REPLY_COMMUNITY_QUESTION.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_COMMUNITY_COMMENT.getType() == notificationDTO.getType())
            return "redirect:/community/question/" + notificationDTO.getOuterId();
        else if (NotificationTypeEnum.REPLY_MUMBLER_ARTICLE.getType() == notificationDTO.getType()
        || NotificationTypeEnum.REPLY_MUMBLER_COMMENT.getType() == notificationDTO.getType()) {
            return "redirect:/mumbler/article/" + notificationDTO.getOuterId();
        } else
            return  "redirect:/";
    }
}
