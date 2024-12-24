package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseHomework;
import com.noa.demo.mapper.CourseHomeworkMapper;
import com.noa.demo.service.CourseHomeworkService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CourseHomeworkServiceImpl extends ServiceImpl<CourseHomeworkMapper, CourseHomework>
        implements CourseHomeworkService {

    @Override
    public Result getHomeworkStats(Long homeworkId) {
        // baseMapper 是 ServiceImpl 中已经注入的 mapper 实例
        Map<String, Object> stats = baseMapper.getHomeworkStats(homeworkId);
        if (stats == null) {
            return Result.fail();
        }
        return Result.suc(stats);
    }
}
