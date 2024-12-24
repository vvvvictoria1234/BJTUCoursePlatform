package com.noa.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.noa.demo.common.Result;
import com.noa.demo.entity.LoginParam;
import com.noa.demo.entity.Menu;
import com.noa.demo.entity.User;
import com.noa.demo.service.LoginService;
import com.noa.demo.service.MenuService;
import com.noa.demo.service.UserService;
import com.noa.demo.utils.JWTUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.reflections.Reflections.log;

@Service
public class LoginServiceImpl implements LoginService {

    private static final String slat = "mszlu!@#";
    @Autowired
    private UserService sysUserService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result login(LoginParam loginParam) {
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();

        // 参数校验
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail();
        }

        // 密码加密
        String pwd = DigestUtils.md5Hex(password);

        // 查询用户
        User sysUser = sysUserService.findUser(account, pwd);
        if (sysUser == null){
            return Result.fail();
        }

        // 查询用户菜单权限
        List<Menu> menuList = menuService.lambdaQuery()
                .like(Menu::getMenuright, sysUser.getRoleId())
                .list();

        // 生成token
        String token = JWTUtils.createToken(sysUser.getId());

        // 构建返回结果
        HashMap<String, Object> res = new HashMap<>();
        res.put("user", sysUser);
        res.put("menu", menuList);
        res.put("token", token);

        // 在存入 Redis 前打印用户信息，确认数据完整性
        log.info("存入Redis的用户信息: {}", JSON.toJSONString(sysUser));
        // 存入Redis
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);

        return Result.success(res);
    }
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }


    public static void main(String[] args) {
        System.out.println(DigestUtils.md5Hex("admin"+slat));
    }
}


