
package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.*;
import com.noa.demo.mapper.ExamArrangementMapper;
import com.noa.demo.mapper.ExamSeatingMapper;
import com.noa.demo.service.ClassroomService;
import com.noa.demo.service.ExamArrangementService;
import com.noa.demo.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import com.noa.demo.exception.ServiceException;
import java.util.List;
import java.util.UUID;

@Service
public class ExamArrangementServiceImpl extends ServiceImpl<ExamArrangementMapper, ExamArrangement>
        implements ExamArrangementService {

    @Autowired
    private ExamSeatingMapper seatingMapper;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ExamService examService;

    @Override
    @Transactional
    public boolean createArrangement(ExamArrangement arrangement) {
        // 验证教室是否可用
        ExamInformation exam = examService.getById(arrangement.getExamId());
        if (exam == null) {
            throw new ServiceException("考试信息不存在");
        }

        // 需要联表查询检查教室是否被占用
        boolean isAvailable = classroomService.isClassroomAvailable(
                arrangement.getClassroomId(),
                exam.getExamDate(),
                exam.getStartTime(),
                exam.getEndTime()
        );

        if (!isAvailable) {
            throw new ServiceException("所选教室在该时间段已被占用");
        }

        return save(arrangement);
    }

    // 检查时间是否冲突
    private boolean isTimeConflict(ExamInformation exam1, ExamInformation exam2) {
        return exam1.getExamDate().equals(exam2.getExamDate()) &&
                ((exam1.getStartTime().compareTo(exam2.getStartTime()) >= 0 &&
                        exam1.getStartTime().compareTo(exam2.getEndTime()) < 0) ||
                        (exam1.getEndTime().compareTo(exam2.getStartTime()) > 0 &&
                                exam1.getEndTime().compareTo(exam2.getEndTime()) <= 0) ||
                        (exam1.getStartTime().compareTo(exam2.getStartTime()) <= 0 &&
                                exam1.getEndTime().compareTo(exam2.getEndTime()) >= 0));
    }

    @Override
    @Transactional
    public boolean generateRandomSeating(Long arrangementId, List<String> studentIds) {
        // 验证考场安排
        ExamArrangement arrangement = getById(arrangementId);
        if (arrangement == null) {
            throw new ServiceException("考场安排不存在");
        }

        // 验证教室信息
        Classroom classroom = classroomService.getById(arrangement.getClassroomId());
        if (classroom == null) {
            throw new ServiceException("教室信息不存在");
        }

        // 清除已有的座位安排
        QueryWrapper<ExamSeating> wrapper = new QueryWrapper<>();
        wrapper.eq("arrangement_id", arrangementId);
        seatingMapper.delete(wrapper);

        // 随机打乱学生顺序
        Collections.shuffle(studentIds);

        // 生成座位安排
        int rowCount = classroom.getRowCount();
        int colCount = classroom.getColumnCount();
        int currentIndex = 0;

        for (int row = 1; row <= rowCount && currentIndex < studentIds.size(); row++) {
            for (int col = 1; col <= colCount && currentIndex < studentIds.size(); col++) {
                ExamSeating seating = new ExamSeating();
                seating.setArrangementId(arrangementId);
                seating.setStudentId(studentIds.get(currentIndex));
                seating.setSeatRow(row);
                seating.setSeatColumn(col);
                seating.setQuestionVersion(generateQuestionVersion());
                seatingMapper.insert(seating);
                currentIndex++;
            }
        }

        return true;
    }

    @Override
    @Transactional
    public boolean generateOrderedSeating(Long arrangementId, List<String> studentIds) {
        // 基本逻辑与随机座位相同，但不打乱学生顺序
        ExamArrangement arrangement = getById(arrangementId);
        if (arrangement == null) {
            throw new ServiceException("考场安排不存在");
        }

        Classroom classroom = classroomService.getById(arrangement.getClassroomId());
        if (classroom == null) {
            throw new ServiceException("教室信息不存在");
        }

        // 清除已有的座位安排
        QueryWrapper<ExamSeating> wrapper = new QueryWrapper<>();
        wrapper.eq("arrangement_id", arrangementId);
        seatingMapper.delete(wrapper);

        // 生成座位安排（按学号顺序）
        int rowCount = classroom.getRowCount();
        int colCount = classroom.getColumnCount();
        int currentIndex = 0;

        for (int row = 1; row <= rowCount && currentIndex < studentIds.size(); row++) {
            for (int col = 1; col <= colCount && currentIndex < studentIds.size(); col++) {
                ExamSeating seating = new ExamSeating();
                seating.setArrangementId(arrangementId);
                seating.setStudentId(studentIds.get(currentIndex));
                seating.setSeatRow(row);
                seating.setSeatColumn(col);
                seating.setQuestionVersion(generateQuestionVersion());
                seatingMapper.insert(seating);
                currentIndex++;
            }
        }

        return true;
    }

    private String generateQuestionVersion() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public List<ExamArrangement> getExamArrangements(Long examId) {
        return baseMapper.getExamArrangements(examId);
    }
}