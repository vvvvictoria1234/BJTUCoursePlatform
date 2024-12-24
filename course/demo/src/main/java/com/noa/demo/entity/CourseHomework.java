package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;


    @Data
    @TableName("course_homework")
    public class CourseHomework {
        @TableId(type = IdType.AUTO)
        private Long homeworkId;

        private Long courseId;
        private String title;
        private String description;
        private String attachmentPath;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;  // 添加开始时间字段

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deadline;

        private Integer totalScore;
        private String publisherId;

        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime publishTime;

        private Integer status;
    }
