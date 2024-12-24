package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamInformation;
import java.util.List;

public interface ExamService extends IService<ExamInformation> {
    // 创建考试
    boolean createExam(ExamInformation exam);

    // 更新考试信息
    boolean updateExam(ExamInformation exam);

    // 获取教师创建的所有考试
    List<ExamInformation> getTeacherExams(String teacherId);

    // 获取课程的所有考试
    List<ExamInformation> getCourseExams(Long courseId);
}
