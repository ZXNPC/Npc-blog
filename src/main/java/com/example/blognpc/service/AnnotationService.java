package com.example.blognpc.service;

import com.example.blognpc.dto.AnnotationDTO;
import com.example.blognpc.mapper.AnnotationMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Annotation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnotationService {
    @Autowired
    private AnnotationMapper annotationMapper;
    @Autowired
    private UserMapper userMapper;

    public AnnotationDTO toAnnotationDTO(Annotation annotation) {
        AnnotationDTO annotationDTO = new AnnotationDTO();
        BeanUtils.copyProperties(annotation, annotationDTO);
        return annotationDTO;
    }

    public AnnotationDTO selectById(Long id) {
        Annotation annotation = annotationMapper.selectById(id);
        return toAnnotationDTO(annotation);
    }
}
