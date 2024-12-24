package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.common.Result;
import com.noa.demo.entity.CourseHomework;

public interface CourseHomeworkService extends IService<CourseHomework> {
    Result getHomeworkStats(Long homeworkId);
}
