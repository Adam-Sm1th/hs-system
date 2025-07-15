package com.tangdeng.hssystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.DepartmentMapper;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.Leave;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.service.DepartmentService;
import com.tangdeng.hssystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    @Autowired
    DepartmentMapper departmentMapper;

    @Override
    public PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper) {
        IPage page = new Page(pageNum, pageSize);
        departmentMapper.selectPage(page, queryWrapper);
        List<Department> records = page.getRecords();
        return new PageBean<>(page.getPages(),page.getCurrent(),page.getSize(), page.getTotal(), records);
    }

    @Override
    public Department selectOneAll(String deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id", deptId);
        return departmentMapper.selectOneAll(queryWrapper);
    }

}
