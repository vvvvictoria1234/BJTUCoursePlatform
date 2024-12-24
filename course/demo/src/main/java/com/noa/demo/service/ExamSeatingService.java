package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamSeating;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamSeating;

import java.util.List;
import java.util.Map;

public interface ExamSeatingService extends IService<ExamSeating> {

    /**
     * 获取考场的所有座位安排信息
     * @param arrangementId 考场安排ID
     * @return 座位安排列表
     */
    List<Map<String, Object>> getArrangementSeating(Long arrangementId);

    /**
     * 获取学生的座位信息
     * @param studentId 学生ID
     * @param examId 考试ID
     * @return 座位信息
     */
    ExamSeating getStudentSeating(String studentId, Long examId);

    /**
     * 生成座位安排
     * @param arrangementId 考场安排ID
     * @param studentIds 学生ID列表
     * @param isRandom 是否随机座位
     * @return 是否成功
     */
    boolean generateSeating(Long arrangementId, List<String> studentIds, boolean isRandom);
}
