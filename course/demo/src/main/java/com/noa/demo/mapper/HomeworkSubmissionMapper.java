package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.entity.HomeworkSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface HomeworkSubmissionMapper extends BaseMapper<HomeworkSubmission> {
    /**
     * 获取作业提交详情，包含学生信息
     * @param submissionId 提交ID
     * @return 包含提交详情和学生信息的Map
     */
    Map<String, Object> getSubmissionDetail(@Param("submissionId") Long submissionId);

    /**
     * 分页查询作业提交列表
     */
    IPage<Map<String, Object>> pageSubmissions(@Param("page") Page<Map<String, Object>> page,
                                               @Param("homeworkId") Long homeworkId);

    /**
     * 查询学生的作业提交记录
     */
    List<Map<String, Object>> listStudentSubmissions(@Param("studentId") String studentId,
                                                     @Param("courseId") Long courseId);
}