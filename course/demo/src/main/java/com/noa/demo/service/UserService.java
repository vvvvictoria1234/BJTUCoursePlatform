package com.noa.demo.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noa.demo.entity.User;


public interface UserService extends IService<User> {

    IPage pageC(IPage<User> page);
    User findUser(String id,String password);

    IPage pageCC(IPage<User> page, Wrapper wrapper);
}
