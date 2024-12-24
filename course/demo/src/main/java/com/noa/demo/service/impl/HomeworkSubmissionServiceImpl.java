package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseHomework;
import com.noa.demo.entity.HomeworkSubmission;
import com.noa.demo.mapper.HomeworkSubmissionMapper;
import com.noa.demo.service.CourseHomeworkService;
import com.noa.demo.service.HomeworkSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class HomeworkSubmissionServiceImpl extends ServiceImpl<HomeworkSubmissionMapper, HomeworkSubmission>
        implements HomeworkSubmissionService {
    @Autowired
    private CourseHomeworkService courseHomeworkService;

    @Override
    public Result submitHomework(HomeworkSubmission submission) {
        // 检查作业是否在提交期限内

        CourseHomework homework = courseHomeworkService.getById(submission.getHomeworkId());
        if (homework == null || LocalDateTime.now().isAfter(homework.getDeadline())) {
            return Result.fail();
        }
        submission.setStatus(0); // 设置为未批改状态
        return save(submission) ? Result.suc() : Result.fail();
    }

    @Override
    public Result gradeHomework(HomeworkSubmission submission) {
        submission.setGradeTime(LocalDateTime.now());
        submission.setStatus(1); // 设置为已批改状态
        return updateById(submission) ? Result.suc() : Result.fail();
    }
    @Override
    public Map<String, Object> getSubmissionDetail(Long submissionId) {
        return baseMapper.getSubmissionDetail(submissionId);
    }


}