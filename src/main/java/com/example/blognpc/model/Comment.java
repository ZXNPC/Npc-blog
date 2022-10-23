package com.example.blognpc.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author NPC
 * @since 2022-10-22
 */
@ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("父类id")
    private Long parentId;

    @ApiModelProperty("父类类型")
    private Integer type;

    private String content;

    @ApiModelProperty("评论人id")
    private Long commentator;

    private Long likeCount;

    private Long commentCount;

    private Long gmtCreate;

    private Long gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Long getCommentator() {
        return commentator;
    }

    public void setCommentator(Long commentator) {
        this.commentator = commentator;
    }
    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", parentId=" + parentId +
            ", type=" + type +
            ", content=" + content +
            ", commentator=" + commentator +
            ", likeCount=" + likeCount +
            ", commentCount=" + commentCount +
            ", gmtCreate=" + gmtCreate +
            ", gmtModified=" + gmtModified +
        "}";
    }
}
