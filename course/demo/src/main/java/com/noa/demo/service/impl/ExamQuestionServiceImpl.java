package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.ExamQuestion;
import com.noa.demo.mapper.ExamQuestionMapper;
import com.noa.demo.service.ExamQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExamQuestionServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion>
        implements ExamQuestionService {

    @Override
    public List<ExamQuestion> getPaperQuestions(Long paperId) {
        return baseMapper.getPaperQuestions(paperId);
    }

    @Override
    public List<ExamQuestion> getQuestionsByType(Long paperId, Integer questionType) {
        return baseMapper.getQuestionsByType(paperId, questionType);
    }

    @Override
    public String getRandomizedOptions(Long questionId, String questionVersion) {
        return baseMapper.getRandomizedOptions(questionId, questionVersion);
    }

    @Override
    @Transactional
    public boolean addQuestions(List<ExamQuestion> questions) {
        // 设置题目顺序
        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setQuestionOrder(i + 1);
        }
        return saveBatch(questions);
    }

    @Override
    public List<ExamQuestion> getQuestionsByExamId(Long examId) {
        // 先获取试卷ID
        return baseMapper.getQuestionsByExamId(examId);
    }
}