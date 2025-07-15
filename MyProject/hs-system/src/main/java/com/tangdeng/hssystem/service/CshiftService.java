package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Cshift;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;

public interface CshiftService extends IService<Cshift> {
    PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper);
}
