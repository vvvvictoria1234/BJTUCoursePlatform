package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noa.demo.entity.CourseHomework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface CourseHomeworkMapper extends BaseMapper<CourseHomework> {
    Map<String, Object> getHomeworkStats(Long homeworkId);
    IPage<Map<String, Object>> listHomeworkWithStats(Page<Map<String, Object>> page,
                                                     @Param("courseId") Long courseId, @Param("ew") QueryWrapper<?> wrapper);
}
