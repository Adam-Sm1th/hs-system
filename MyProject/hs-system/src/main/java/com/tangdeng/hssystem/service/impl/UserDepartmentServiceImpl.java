package com.tangdeng.hssystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.UserDepartmentMapper;
import com.tangdeng.hssystem.pojo.entity.UserDepartment;
import com.tangdeng.hssystem.service.UserDepartmentService;
import org.springframework.stereotype.Service;

@Service
public class UserDepartmentServiceImpl extends ServiceImpl<UserDepartmentMapper, UserDepartment> implements UserDepartmentService {
}
