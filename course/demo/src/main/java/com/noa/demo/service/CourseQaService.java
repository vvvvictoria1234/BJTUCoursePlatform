package com.noa.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.CourseQaRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseQaService extends IService<CourseQaRecord> {
    /**
     * AI问答
     */
    CourseQaRecord askQuestion(Long courseId, String studentId, String question);

    /**
     * 获取学生的问答历史记录
     */
    List<CourseQaRecord> getStudentHistory(String studentId, Long courseId);

    /**
     * 分页查询课程的问答记录
     */
    IPage<CourseQaRecord> getPageByCourse(Long courseId, Integer pageNum, Integer pageSize);

    /**
     * 根据时间范围查询问答记录
     */
    List<CourseQaRecord> getByTimeRange(Long courseId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除问答记录
     */
    boolean deleteQaRecord(Long qaId);

    /**
     * 统计课程问答数量
     */
    int countByCourse(Long courseId);

    /**
     * 统计学生问答数量
     */
    int countByStudent(String studentId, Long courseId);
}