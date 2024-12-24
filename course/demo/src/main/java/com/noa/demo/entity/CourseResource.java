package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_resource")
public class CourseResource {
    @TableId(type = IdType.AUTO)
    private Long resourceId;
    private String courseId;
    private String fileName;
    private String filePath;
    private String fileType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;
}