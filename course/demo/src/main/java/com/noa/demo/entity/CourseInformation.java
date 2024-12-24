package com.noa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_information")
public class CourseInformation {
    @TableId(type = IdType.AUTO)  // 改为自增
    private String courseId;
    private String calendar;
    private String courseDescription;
    private String courseName;
    private String syllabus;
    private String teacherId;
}
