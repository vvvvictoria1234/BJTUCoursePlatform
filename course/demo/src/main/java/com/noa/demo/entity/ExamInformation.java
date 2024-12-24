package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("exam_information")
public class ExamInformation {
    @TableId(type = IdType.AUTO)
    private Long examId;

    private Long courseId;
    private String title;
    private String description;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer totalScore;
    private String teacherId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Integer status;
}
