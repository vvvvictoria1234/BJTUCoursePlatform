package com.noa.demo.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.ExamQuestion;

import java.util.List;

public interface ExamQuestionService extends IService<ExamQuestion> {
    /**
     * 获取试卷的所有试题
     * @param paperId 试卷ID
     * @return 试题列表
     */
    List<ExamQuestion> getPaperQuestions(Long paperId);

    /**
     * 获取试卷的指定类型试题
     * @param paperId 试卷ID
     * @param questionType 题目类型
     * @return 试题列表
     */
    List<ExamQuestion> getQuestionsByType(Long paperId, Integer questionType);

    /**
     * 根据题号获取乱序后的选项
     * @param questionId 试题ID
     * @param questionVersion 试题版本
     * @return 乱序后的选项
     */
    String getRandomizedOptions(Long questionId, String questionVersion);

    /**
     * 批量添加试题
     * @param questions 试题列表
     * @return 是否成功
     */
    boolean addQuestions(List<ExamQuestion> questions);

    /**
     * 根据考试ID获取所有试题
     * @param examId 考试ID
     * @return 试题列表
     */
    List<ExamQuestion> getQuestionsByExamId(Long examId);
}
