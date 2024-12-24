package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.ExamArrangement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamArrangementMapper extends BaseMapper<ExamArrangement> {
    List<ExamArrangement> getExamArrangements(Long examId);

}
