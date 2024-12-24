package com.noa.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {

    List<ExamQuestion> getPaperQuestions(@Param("paperId") Long paperId);

    List<ExamQuestion> getQuestionsByType(@Param("paperId") Long paperId, @Param("questionType") Integer questionType);

    String getRandomizedOptions(@Param("questionId") Long questionId, @Param("questionVersion") String questionVersion);

    List<ExamQuestion> getQuestionsByExamId(@Param("examId") Long examId);
}
