package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.AnnotationDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.AnnotationMapper;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Annotation;
import com.example.blognpc.model.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnotationService {
    @Autowired
    private AnnotationMapper annotationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DraftMapper draftMapper;

    public AnnotationDTO toAnnotationDTO(Annotation annotation) {
        User user = userMapper.selectById(annotation.getCreator());
        if (user == null) {
            throw new CustomizeException(CustomizeErrorCode.USER_NOT_FOUND);
        }

        AnnotationDTO annotationDTO = new AnnotationDTO();
        BeanUtils.copyProperties(annotation, annotationDTO);
        annotationDTO.setUser(user);
        return annotationDTO;
    }

    public AnnotationDTO selectById(Long id) {
        Annotation annotation = annotationMapper.selectById(id);
        return toAnnotationDTO(annotation);
    }

    /**
     * allow null
     *
     * @param outerId
     * @return
     */
    public AnnotationDTO selectByOuterId(Long outerId) {
        List<Annotation> annotations = annotationMapper.selectList(new QueryWrapper<Annotation>().eq("outer_id", outerId));
        Annotation annotation = annotations.size() == 0 ? null : annotations.get(0);
        if (annotation != null) {
            // 有注释
            return toAnnotationDTO(annotation);
        } {
            // 无注释
            return null;
        }
    }

    public void createOrUpdate(Annotation annotation, Long draftId) {
        if (annotation.getId() == null) {
            // 创建注释
            annotation.setGmtCreate(System.currentTimeMillis());
            annotation.setGmtModified(annotation.getGmtCreate());
            annotationMapper.insert(annotation);
            if (draftId != null) {
                draftMapper.deleteById(draftId);
            }
        } else {
            // 更新注释
            Annotation dbAnnotation = annotationMapper.selectById(annotation.getId());
            if (dbAnnotation == null) {
                throw new CustomizeException(CustomizeErrorCode.ANNO_NOT_FOUND);
            }

            if (dbAnnotation.getCreator() != annotation.getCreator()) {
                throw new CustomizeException(CustomizeErrorCode.ACCOUNT_ERROR);
            }

            annotation.setGmtCreate(dbAnnotation.getGmtCreate());
            annotation.setGmtModified(System.currentTimeMillis());
            annotationMapper.updateById(annotation);
            if (draftId != null) {
                draftMapper.deleteById(draftId);
            }
        }
    }

    public void deleteByOuterId(Long id) {
        annotationMapper.delete(new QueryWrapper<Annotation>().eq("outer_id", id));
    }
}
