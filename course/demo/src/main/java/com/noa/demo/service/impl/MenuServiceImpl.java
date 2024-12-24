package com.noa.demo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noa.demo.entity.Menu;
import com.noa.demo.mapper.MenuMapper;
import com.noa.demo.service.MenuService;
import org.springframework.stereotype.Service;


@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

}