package com.example.blognpc.resolver;

import com.alibaba.fastjson.JSON;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CustomizeExceptionResolver implements ErrorViewResolver {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request,
                        Throwable e,
                        Model model,
                        HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else if (e instanceof LoginException) {
                resultDTO = ResultDTO.errorOf((LoginException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ex) {
            }
            return null;
        } else {
            String message;
            if (e instanceof CustomizeException) {
                message = e.getMessage();
            } else if (e instanceof LoginException) {
                message = e.getMessage();
            } else {
                message = CustomizeErrorCode.SYSTEM_ERROR.getMessage();
            }
            model.addAttribute("message", message);
            return new ModelAndView("error");
        }
    }

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "请求的页面出错了！");
        return modelAndView;
    }
}
