package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("exam_seating")
public class ExamSeating {
    @TableId(type = IdType.AUTO)
    private Long seatingId;

    private Long arrangementId;
    private String studentId;
    private Integer seatRow;
    private Integer seatColumn;
    private String questionVersion;
}