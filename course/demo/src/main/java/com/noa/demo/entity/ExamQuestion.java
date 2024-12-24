package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_question")
public class ExamQuestion {
    @TableId(type = IdType.AUTO)
    private Long questionId;

    private Long paperId;
    private Integer questionType;
    private String questionContent;
    private String standardAnswer;
    private Double score;
    private Integer questionOrder;
    private String optionsJson;
}