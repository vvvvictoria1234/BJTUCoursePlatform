package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.ExamInformation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamInformationMapper extends BaseMapper<ExamInformation> {

    List<ExamInformation> getTeacherExams(String teacherId);

    List<ExamInformation> getCourseExams(Long courseId);
}
