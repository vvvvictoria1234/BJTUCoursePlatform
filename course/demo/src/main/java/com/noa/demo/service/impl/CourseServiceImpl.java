package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.CourseInformation;
import com.noa.demo.entity.User;
import com.noa.demo.mapper.CourseInformationMapper;
import com.noa.demo.mapper.CourseSelectionMapper;
import com.noa.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseInformationMapper, CourseInformation> implements CourseService {
    @Autowired
    private CourseSelectionMapper courseSelectionMapper;

    @Override
    public CourseInformation getCourseDetail(String courseId) {
        return this.getById(courseId);
    }

    @Override
    public boolean updateCourseInfo(CourseInformation course) {
        return this.updateById(course);
    }

    @Override
    public List<User> getCourseStudents(String courseId) {
        return courseSelectionMapper.getStudentsByCourseId(courseId);
    }
    @Override
    public List<CourseInformation> getTeacherCourses(String teacherId) {
        return baseMapper.getTeacherCourses(teacherId);
    }

    @Override
    public List<CourseInformation> getStudentCourses(String studentId) {
        return baseMapper.getStudentCourses(studentId);
    }
}
