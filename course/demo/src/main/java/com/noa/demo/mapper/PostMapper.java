package com.noa.demo.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.noa.demo.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import javax.management.Query;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    Query lambdaUpdate();
}

