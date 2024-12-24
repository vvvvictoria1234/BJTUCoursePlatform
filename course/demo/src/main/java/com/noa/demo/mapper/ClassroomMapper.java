package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.noa.demo.entity.Classroom;
import com.noa.demo.entity.ExamArrangement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {
    List<Classroom> getAvailableClassrooms();

    Integer checkClassroomAvailable(@Param(Constants.WRAPPER) Wrapper wrapper);
}
