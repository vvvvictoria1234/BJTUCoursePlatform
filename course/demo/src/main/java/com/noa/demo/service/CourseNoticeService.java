package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.CourseNotice;

import java.util.List;

public interface CourseNoticeService extends IService<CourseNotice> {
    // 发布通知
    boolean publishNotice(CourseNotice notice);
    // 获取课程通知列表
    List<CourseNotice> getCourseNotices(String courseId);
}
