package com.example.blognpc.controller;


import com.example.blognpc.dto.FileDTO;
import com.example.blognpc.provider.TencentCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private TencentCloudProvider tencentCloudProvider;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload(HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        String fileName = tencentCloudProvider.upload(file.getInputStream(), file.getOriginalFilename());
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl(fileName);
        return fileDTO;
    }
}
