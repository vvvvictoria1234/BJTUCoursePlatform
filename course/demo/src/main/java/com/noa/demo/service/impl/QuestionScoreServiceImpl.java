package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.QuestionScore;
import com.noa.demo.mapper.QuestionScoreMapper;
import com.noa.demo.service.QuestionScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionScoreServiceImpl extends ServiceImpl<QuestionScoreMapper, QuestionScore>
        implements QuestionScoreService {

    @Override
    public List<QuestionScore> getAnswerScores(Long answerId) {
        return baseMapper.getAnswerScores(answerId);
    }

    @Override
    @Transactional
    public boolean saveScore(QuestionScore score) {
        return save(score);
    }

    @Override
    @Transactional
    public boolean saveBatch(List<QuestionScore> scores) {
        return super.saveBatch(scores);
    }

    @Override
    public List<Map<String, Object>> getScoreDistribution(Long examId) {
        return baseMapper.getScoreDistribution(examId);
    }

    @Override
    public Map<Long, Double> calculateQuestionCorrectRates(Long examId) {
        List<QuestionScore> scores = baseMapper.getExamScores(examId);

        // 按题目ID分组，计算每题的正确率
        Map<Long, Double> correctRates = scores.stream()
                .collect(Collectors.groupingBy(
                        QuestionScore::getQuestionId,
                        Collectors.averagingDouble(score ->
                                score.getIsCorrect() != null && score.getIsCorrect() == 1 ? 1.0 : 0.0)
                ));

        return correctRates;
    }
}
