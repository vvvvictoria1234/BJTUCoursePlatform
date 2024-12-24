package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.CourseInformation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseInformationMapper extends BaseMapper<CourseInformation> {
    /**
     * 根据课程ID获取课程名称
     */
    @Select("SELECT course_name FROM course_information WHERE course_id = #{courseId}")
    String getCourseName(@Param("courseId") Long courseId);
    List<CourseInformation> getTeacherCourses(String teacherId);
    List<CourseInformation> getStudentCourses(String studentId);
}