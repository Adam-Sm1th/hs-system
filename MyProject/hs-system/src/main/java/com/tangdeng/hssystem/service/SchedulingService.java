package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Scheduling;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.SchedulingVO;

import java.util.Date;
import java.util.List;

public interface SchedulingService extends IService<Scheduling> {
    PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper);

    List<Scheduling> getByUserId(String userId, Date scheDate);

    List<SchedulingVO> getAllByUserId(String userId,String deptId);

    List<SchedulingVO> getScheVOList(List<Scheduling> list);
}
