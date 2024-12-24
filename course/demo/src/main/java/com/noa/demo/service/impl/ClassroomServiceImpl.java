package com.noa.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.Classroom;
import com.noa.demo.entity.ExamArrangement;
import com.noa.demo.mapper.ClassroomMapper;
import com.noa.demo.service.ClassroomService;
import com.noa.demo.service.ExamArrangementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements ClassroomService {

    @Autowired
    private ExamArrangementService examArrangementService;  // 注入ExamArrangementService

    @Override
    public List<Classroom> getAvailableClassrooms() {
        return baseMapper.getAvailableClassrooms();
    }

    @Override
    public boolean isClassroomAvailable(Long classroomId, LocalDate examDate,
                                        LocalTime startTime, LocalTime endTime) {
        // 联表查询检查该教室在指定时间是否已被占用
        QueryWrapper<ExamArrangement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ea.classroom_id", classroomId)
                .eq("ei.exam_date", examDate)
                .and(wrapper -> {
                    wrapper.and(w -> {
                        w.le("ei.start_time", startTime)
                                .ge("ei.end_time", startTime);
                    }).or(w -> {
                        w.le("ei.start_time", endTime)
                                .ge("ei.end_time", endTime);
                    });
                });

        return baseMapper.checkClassroomAvailable(queryWrapper) == 0;
    }
}