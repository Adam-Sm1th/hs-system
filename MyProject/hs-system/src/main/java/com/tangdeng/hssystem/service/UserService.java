package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.entity.User;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> getByEmail(String email);
    PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper);
    User selectOneAll(String userId);
}
