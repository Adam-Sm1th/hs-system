package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;

public interface DepartmentService extends IService<Department> {
    PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper);

    Department selectOneAll(String deptId);
}
