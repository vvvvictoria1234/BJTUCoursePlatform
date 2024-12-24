package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_answer")
public class StudentAnswer {
    @TableId(type = IdType.AUTO)
    private Long answerId;

    private Long examId;
    private String studentId;
    private String answerPaperPath;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;

    private Double totalScore;
    private Integer status;
}
