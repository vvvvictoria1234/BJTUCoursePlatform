package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_bookmarks")
public class PostBookmark {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private String userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}