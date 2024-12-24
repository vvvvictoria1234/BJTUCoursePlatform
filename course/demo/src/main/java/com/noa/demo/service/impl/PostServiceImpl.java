package com.noa.demo.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.noa.demo.entity.Post;
import com.noa.demo.mapper.PostMapper;
import com.noa.demo.service.PostService;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
}

