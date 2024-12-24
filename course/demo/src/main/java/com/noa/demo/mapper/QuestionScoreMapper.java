package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.QuestionScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionScoreMapper extends BaseMapper<QuestionScore> {

    List<QuestionScore> getAnswerScores(@Param("answerId") Long answerId);

    List<Map<String, Object>> getScoreDistribution(@Param("examId") Long examId);

    List<QuestionScore> getExamScores(@Param("examId") Long examId);
}