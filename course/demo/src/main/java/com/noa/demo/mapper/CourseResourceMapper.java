package com.noa.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.CourseNotice;
import com.noa.demo.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noa.demo.entity.CourseResource;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CourseResourceMapper extends BaseMapper<CourseResource> {
    CourseResource selectById(Long resourceId);
}
