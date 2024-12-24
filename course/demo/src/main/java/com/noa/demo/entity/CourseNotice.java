package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_notice")
public class CourseNotice {
    @TableId(type = IdType.AUTO)
    private Long noticeId;
    private String courseId;
    private String title;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime publishTime;

    private String publisherId;
}
