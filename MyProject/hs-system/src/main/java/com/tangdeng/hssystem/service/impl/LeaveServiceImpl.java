package com.tangdeng.hssystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.LeaveMapper;
import com.tangdeng.hssystem.pojo.entity.Leave;
import com.tangdeng.hssystem.pojo.entity.Scheduling;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveServiceImpl extends ServiceImpl<LeaveMapper, Leave> implements LeaveService {
    @Autowired
    LeaveMapper leaveMapper;

    @Override
    public PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper) {
        IPage page = new Page(pageNum, pageSize);
        leaveMapper.selectPage(page, queryWrapper);
        List<Leave> records = page.getRecords();
        return new PageBean<>(page.getPages(),page.getCurrent(),page.getSize(), page.getTotal(), records);

    }
}
