package com.example.blognpc.controller;


import com.example.blognpc.dto.CommentCreateDTO;
import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.model.Comment;
import com.example.blognpc.model.User;
import com.example.blognpc.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author NPC
 * @since 2022-08-23
 */
@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ResponseBody
    @PostMapping("/comment")
    public ResultDTO post(@RequestBody CommentCreateDTO commentCreateDTO,
                          HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);

        if (StringUtils.isBlank(commentCreateDTO.getContent()))
            throw new CustomizeException(CustomizeErrorCode.CONTENT_IS_EMPTY);

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setType(commentCreateDTO.getType());
        comment.setContent(commentCreateDTO.getContent());
        comment.setCommentator(user.getId());
        commentService.insert(comment);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMUNITY_COMMENT);
        commentDTOS.addAll(commentService.listByTargetId(id, CommentTypeEnum.MUMBLER_COMMENT));
        return ResultDTO.okOf(commentDTOS);
    }
}
