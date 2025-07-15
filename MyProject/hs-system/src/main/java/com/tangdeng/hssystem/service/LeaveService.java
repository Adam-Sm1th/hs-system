package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Leave;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;

public interface LeaveService extends IService<Leave> {
    PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper);
}
