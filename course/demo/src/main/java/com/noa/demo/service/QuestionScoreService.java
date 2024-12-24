package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.QuestionScore;

import java.util.List;
import java.util.Map;

public interface QuestionScoreService extends IService<QuestionScore> {
    /**
     * 获取答卷的所有题目得分
     * @param answerId 答卷ID
     * @return 得分列表
     */
    List<QuestionScore> getAnswerScores(Long answerId);

    /**
     * 保存题目得分
     * @param score 得分信息
     * @return 是否成功
     */
    boolean saveScore(QuestionScore score);

    /**
     * 批量保存题目得分
     * @param scores 得分列表
     * @return 是否成功
     */
    boolean saveBatch(List<QuestionScore> scores);

    /**
     * 统计考试成绩分布
     * @param examId 考试ID
     * @return 分数段分布
     */
    List<Map<String, Object>> getScoreDistribution(Long examId);

    /**
     * 计算试题正确率
     * @param examId 考试ID
     * @return 各题正确率
     */
    Map<Long, Double> calculateQuestionCorrectRates(Long examId);
}
