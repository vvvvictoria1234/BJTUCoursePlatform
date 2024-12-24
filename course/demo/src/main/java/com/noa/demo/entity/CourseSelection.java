package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_selection")
public class CourseSelection {
    @TableId(type = IdType.AUTO)
    private Long selectionId;
    private String courseId;
    private String studentId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime selectTime;
    private Integer status;
}