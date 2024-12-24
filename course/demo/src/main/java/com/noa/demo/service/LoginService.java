package com.noa.demo.service;

import com.noa.demo.common.Result;
import com.noa.demo.entity.LoginParam;
import com.noa.demo.entity.User;

public interface LoginService {

    Result login(LoginParam loginParam);
    //User checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

}
