package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.CourseInformation;
import com.noa.demo.entity.User;

import java.util.List;

public interface CourseService extends IService<CourseInformation> {
    // 获取课程详细信息
    CourseInformation getCourseDetail(String courseId);
    // 更新课程信息
    boolean updateCourseInfo(CourseInformation course);
    // 获取选课学生列表
    List<User> getCourseStudents(String courseId);
    List<CourseInformation> getTeacherCourses(String teacherId);
    List<CourseInformation> getStudentCourses(String studentId);
}
