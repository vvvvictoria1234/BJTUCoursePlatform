package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_paper")
public class ExamPaper {
    @TableId(type = IdType.AUTO)
    private Long paperId;

    private Long examId;
    private String paperTemplatePath;
    private String answerTemplatePath;
    private String ocrConfig;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}