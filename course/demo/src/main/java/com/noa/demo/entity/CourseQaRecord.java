package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_qa_record")
public class CourseQaRecord {
    @TableId(type = IdType.AUTO)
    private Long qaId;

    private Long courseId;

    private String studentId;

    private String question;

    private String answer;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
