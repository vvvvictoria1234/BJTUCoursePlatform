package com.noa.demo.controller;


import com.noa.demo.common.Result;
import com.noa.demo.entity.LoginParam;
import com.noa.demo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam){

        return loginService.login(loginParam);

    }
}

