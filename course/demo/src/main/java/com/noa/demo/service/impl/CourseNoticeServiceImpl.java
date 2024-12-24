package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.CourseNotice;
import com.noa.demo.mapper.CourseNoticeMapper;
import com.noa.demo.service.CourseNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseNoticeServiceImpl extends ServiceImpl<CourseNoticeMapper, CourseNotice> implements CourseNoticeService {

    @Override
    public boolean publishNotice(CourseNotice notice) {
        return this.save(notice);
    }

    @Override
    public List<CourseNotice> getCourseNotices(String courseId) {
        LambdaQueryWrapper<CourseNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseNotice::getCourseId, courseId)
                .orderByDesc(CourseNotice::getPublishTime);
        return this.list(wrapper);
    }
}
