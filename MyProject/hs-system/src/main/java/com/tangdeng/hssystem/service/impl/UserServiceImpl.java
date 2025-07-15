package com.tangdeng.hssystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.UserMapper;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.User;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.repository.query.ExampleQueryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;
    @Override
    public List<User> getByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_email", email);
        List<User> users = userMapper.selectList(wrapper);
        return users;
    }

    @Override
    public PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper) {
        IPage page = new Page(pageNum, pageSize);
        userMapper.selectPage(page, queryWrapper);
        List<User> records = page.getRecords();
        return new PageBean<>(page.getPages(),page.getCurrent(),page.getSize(), page.getTotal(), records);
    }

    @Override
    public User selectOneAll(String userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return userMapper.selectOneAll(queryWrapper);
    }
}
