package com.noa.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.Classroom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ClassroomService extends IService<Classroom> {
    // 获取所有可用教室
    List<Classroom> getAvailableClassrooms();

    // 检查教室在指定时间段是否可用
    boolean isClassroomAvailable(Long classroomId, LocalDate date, LocalTime startTime, LocalTime endTime);
}