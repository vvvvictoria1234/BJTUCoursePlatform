package com.noa.demo.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("homework_submission")
public class HomeworkSubmission {
    @TableId(type = IdType.AUTO)
    private Long submissionId;
    private Long homeworkId;
    private String studentId;
    private String content;
    private String attachmentPath;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;
    private Integer score;
    private String feedback;
    private LocalDateTime gradeTime;
    private Integer status;

    public void setHomeworkId(Object value) {
        if (value == null) {
            this.homeworkId = null;
        } else if (value instanceof Long) {
            this.homeworkId = (Long) value;
        } else if (value instanceof Integer) {
            this.homeworkId = Long.valueOf((Integer) value);
        } else if (value instanceof String) {
            try {
                this.homeworkId = Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid homeworkId format");
            }
        } else {
            throw new IllegalArgumentException("Unsupported homeworkId type");
        }
    }
}