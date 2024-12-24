package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("question_score")
public class QuestionScore {
    @TableId(type = IdType.AUTO)
    private Long scoreId;

    private Long answerId;
    private Long questionId;
    private String studentAnswer;
    private Double score;
    private Integer isCorrect;
    private String comment;
}
