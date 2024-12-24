package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_arrangement")
public class ExamArrangement {
    @TableId(type = IdType.AUTO)
    private Long arrangementId;

    private Long examId;
    private Long classroomId;
    private Integer isRandomSeating;
    private String invigilatorId;
}
