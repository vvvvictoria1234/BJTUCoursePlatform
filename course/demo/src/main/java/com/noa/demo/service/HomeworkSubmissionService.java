package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.common.Result;
import com.noa.demo.entity.HomeworkSubmission;

import java.util.Map;

public interface HomeworkSubmissionService extends IService<HomeworkSubmission> {
    Result submitHomework(HomeworkSubmission submission);
    Result gradeHomework(HomeworkSubmission submission);
    /**
     * 获取作业提交详情
     * @param submissionId 提交ID
     * @return 提交详情和学生信息
     */
    Map<String, Object> getSubmissionDetail(Long submissionId);

}
