package com.noa.demo.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.ExamInformation;
import com.noa.demo.mapper.ExamInformationMapper;
import com.noa.demo.service.ExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamInformationMapper, ExamInformation> implements ExamService {

    @Override
    @Transactional
    public boolean createExam(ExamInformation exam) {
        // 设置初始状态
        exam.setStatus(0); // 未开始
        return save(exam);
    }

    @Override
    @Transactional
    public boolean updateExam(ExamInformation exam) {
        return updateById(exam);
    }

    @Override
    public List<ExamInformation> getTeacherExams(String teacherId) {
        return baseMapper.getTeacherExams(teacherId);
    }

    @Override
    public List<ExamInformation> getCourseExams(Long courseId) {
        return baseMapper.getCourseExams(courseId);
    }

}
