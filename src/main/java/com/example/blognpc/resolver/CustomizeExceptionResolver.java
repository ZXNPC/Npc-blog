package com.example.blognpc.resolver;

import com.alibaba.fastjson.JSON;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.exception.VerifyException;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
@ControllerAdvice

public class CustomizeExceptionResolver implements ErrorViewResolver {

    // 处理登录错误
    @ExceptionHandler(value = {LoginException.class})
    public String loginExceptionHandler(Exception e, Model model) {
        model.addAttribute("resultDTO", ResultDTO.errorOf((LoginException) e));
        model.addAttribute("fromSignin", ((LoginException) e).is1xxSigninError() ? true : false);
        return "login";
    }

    @ExceptionHandler(value = {VerifyException.class})
    public String verifyExceptionHandler(Exception e, Model model) {
        model.addAttribute("resultDTO", ResultDTO.errorOf((LoginException) e));
        return "login-verify";
    }

    // 处理通用错误
    @ExceptionHandler(Exception.class)
    public ModelAndView handle(HttpServletRequest request,
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
            } catch (IOException ex) {}
            return null;
        } else {
            String message;
            if (e instanceof CustomizeException) {
                message = e.getMessage();
            } else if (e instanceof LoginException) {
                message = e.getMessage();
            } else {
                e.printStackTrace();
                message = CustomizeErrorCode.SYSTEM_ERROR.getMessage();
            }
            model.addAttribute("message", message);
            return new ModelAndView("error");
        }
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        ModelAndView modelAndView = new ModelAndView("error");
        if (status.is4xxClientError()) {
            modelAndView.addObject("message", "请求出错，换一个呗？");
        }
        if (status.is5xxServerError()) {
            modelAndView.addObject("message", "服务器冒烟了。");
        }
        return modelAndView;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    // 处理异常页面访问
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", "请求的页面出错了！");
        return modelAndView;
    }
}
