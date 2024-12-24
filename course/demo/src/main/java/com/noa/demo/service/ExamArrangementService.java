package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamArrangement;

import java.util.List;

public interface ExamArrangementService extends IService<ExamArrangement> {
    // 创建考场安排
    boolean createArrangement(ExamArrangement arrangement);

    // 随机生成座位安排
    boolean generateRandomSeating(Long arrangementId, List<String> studentIds);

    // 按顺序生成座位安排
    boolean generateOrderedSeating(Long arrangementId, List<String> studentIds);

    // 获取考试的所有考场安排
    List<ExamArrangement> getExamArrangements(Long examId);
}
