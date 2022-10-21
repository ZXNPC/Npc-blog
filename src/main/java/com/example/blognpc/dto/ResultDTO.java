package com.example.blognpc.dto;

import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.ICustomizeErrorCode;
import com.example.blognpc.exception.LoginException;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDTO errorOf(Integer code, String message) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(ICustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(), errorCode.getMessage());
    }

    public static ResultDTO errorOf(Exception e) {
        return errorOf(CustomizeErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    public static ResultDTO okOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }

    public static ResultDTO emailOkOf(String email) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("验证邮件已发送至 " + email + " ，请注意查收");
        return resultDTO;
    }

    public static <T> ResultDTO okOf(T data) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(data);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeException e) {
        return errorOf(e.getCode(), e.getMessage());
    }

    public static ResultDTO errorOf(LoginException e) {
        return errorOf(e.getCode(), e.getMessage());
    }
}
