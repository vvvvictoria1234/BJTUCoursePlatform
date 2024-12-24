package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.ExamSeating;
import org.apache.ibatis.annotations.Mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.ExamSeating;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 考生座位安排Mapper接口
 */
@Mapper
public interface ExamSeatingMapper extends BaseMapper<ExamSeating> {

    /**
     * 获取考场的所有座位安排
     * @param arrangementId 考场安排ID
     * @return 座位安排列表（包含学生信息）
     */
    List<Map<String, Object>> getArrangementSeating(@Param("arrangementId") Long arrangementId);

    /**
     * 获取学生的座位信息
     * @param studentId 学生ID
     * @param examId 考试ID
     * @return 座位信息
     */
    ExamSeating getStudentSeating(@Param("studentId") String studentId, @Param("examId") Long examId);
}