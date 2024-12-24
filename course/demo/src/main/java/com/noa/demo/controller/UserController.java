package com.noa.demo.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.noa.demo.common.QueryPageParam;
import com.noa.demo.common.Result;
import com.noa.demo.entity.User;
import com.noa.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.User;
import com.noa.demo.mapper.UserMapper;
import com.noa.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.reflections.Reflections.log;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }


    //新增
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        return userService.save(user) ? Result.suc() : Result.fail();
    }

    //更新
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        return userService.updateById(user) ? Result.suc() : Result.fail();
    }

    //删除
    @GetMapping("/del")
    public Result del(@RequestParam String id) {
        return userService.removeById(id) ? Result.suc() : Result.fail();
    }

    //查询（模糊、匹配）
    @PostMapping("/listP")
    public Result listP(@RequestBody User user){
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        if(StringUtils.isNotBlank(user.getName())){
            lambdaQueryWrapper.like(User::getName,user.getName());
        }

        return Result.suc(userService.list(lambdaQueryWrapper));
    }

    @PostMapping("/listPage")
    public List<User> listPage(@RequestBody QueryPageParam query){
        HashMap param = query.getParam();
        String name = (String)param.get("name");
        System.out.println("name==="+(String)param.get("name"));

        Page<User> page = new Page();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.like(User::getName,name);


        IPage result = userService.page(page,lambdaQueryWrapper);

        System.out.println("total=="+result.getTotal());

        return result.getRecords();
    }
    @GetMapping("/info")
    public Result getUserInfo(@RequestHeader("Authorization") String token) {
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer ")) {
            return Result.fail();
        }

        token = token.substring(7);
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);

        if (userJson == null) {
            return Result.fail();
        }

        // 打印从Redis获取的原始JSON，用于调试
        log.info("从Redis获取的用户信息: {}", userJson);

        User user = JSON.parseObject(userJson, User.class);

        // 如果Redis中的信息不完整，可以从数据库重新查询
        if (user.getRoleId() == null || user.getPassword() == null) {
            User fullUser = userService.getById(user.getId());
            if (fullUser != null) {
                // 更新Redis中的信息
                redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(fullUser), 1, TimeUnit.DAYS);
                return Result.success(fullUser);
            }
        }

        return Result.success(user);
    }


    @PostMapping("/listPageC1")
    public Result listPageC1(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String name = (String) param.get("name");
        String id = (String) param.get("id");

        Page<User> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 动态添加条件：name 模糊查询
        if (StringUtils.isNotBlank(name) && !"null".equals(name)) {
            lambdaQueryWrapper.like(User::getName, name);
        }
        // 动态添加条件：id 精确查询
        if (StringUtils.isNotBlank(id)) {
            lambdaQueryWrapper.eq(User::getId, id);
        }

        // 执行分页查询
        IPage<User> result = userService.pageCC(page, lambdaQueryWrapper);

        System.out.println("total==" + result.getTotal());

        // 返回结果
        return Result.suc(result.getRecords(), result.getTotal());
    }




}
