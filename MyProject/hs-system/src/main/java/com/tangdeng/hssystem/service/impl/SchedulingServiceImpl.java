package com.tangdeng.hssystem.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.SchedulingMapper;
import com.tangdeng.hssystem.mapper.ShiftMapper;
import com.tangdeng.hssystem.mapper.UserMapper;
import com.tangdeng.hssystem.pojo.entity.Scheduling;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.SchedulingVO;
import com.tangdeng.hssystem.pojo.vo.ShiftVO;
import com.tangdeng.hssystem.pojo.vo.UserVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulingServiceImpl extends ServiceImpl<SchedulingMapper, Scheduling> implements SchedulingService{
    @Autowired
    SchedulingMapper schedulingMapper;
    @Autowired
    ShiftMapper shiftMapper;
    @Autowired
    UserMapper userMapper;


    @Override
    public PageBean getPages(Integer pageNum, Integer pageSize, QueryWrapper queryWrapper) {
        IPage page = new Page(pageNum, pageSize);
        schedulingMapper.selectPage(page, queryWrapper);

        List<Scheduling> records = page.getRecords();
        List<SchedulingVO> collect = records.stream()
                .map(item -> {
                    SchedulingVO schedulingVO = BeanUtil.copyProperties(item, SchedulingVO.class);
                    QueryWrapper shiftQueryWrapper = new QueryWrapper<>();
                    shiftQueryWrapper.eq("shift_id", item.getShiftId());
                    QueryWrapper userQueryWrapper = new QueryWrapper();
                    userQueryWrapper.eq("user_id", item.getUserId());
                    schedulingVO.setShiftVO(BeanUtil.copyProperties(shiftMapper.selectOneAll(shiftQueryWrapper), ShiftVO.class));
                    schedulingVO.setUserVO(BeanUtil.copyProperties(userMapper.selectOneAll(userQueryWrapper), UserVO.class));
                    return schedulingVO;
                })
                .collect(Collectors.toList());

        return new PageBean<>(page.getPages(),page.getCurrent(),page.getSize(), page.getTotal(), collect);
    }


    @Override
    public List<Scheduling> getByUserId(String userId, Date scheDate) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sche_date", scheDate);

        List list = schedulingMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<SchedulingVO> getAllByUserId(String userId,String deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        if(deptId != null) {
            queryWrapper.eq("dept_id", deptId);
        }


        List<Scheduling> records = schedulingMapper.selectList(queryWrapper);
        List<SchedulingVO> collect = records.stream()
                .map(item -> {
                    SchedulingVO schedulingVO = BeanUtil.copyProperties(item, SchedulingVO.class);
                    QueryWrapper shiftQueryWrapper = new QueryWrapper<>();
                    shiftQueryWrapper.eq("shift_id", item.getShiftId());
                    schedulingVO.setShiftVO(BeanUtil.copyProperties(shiftMapper.selectOneAll(shiftQueryWrapper), ShiftVO.class));
                    schedulingVO.setUserVO(BeanUtil.copyProperties(userMapper.selectById(item.getUserId()), UserVO.class));
                    return schedulingVO;
                })
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<SchedulingVO> getScheVOList(List<Scheduling> records) {
        List<SchedulingVO> collect = records.stream()
                .map(item -> {
                    SchedulingVO schedulingVO = BeanUtil.copyProperties(item, SchedulingVO.class);
                    QueryWrapper shiftQueryWrapper = new QueryWrapper<>();
                    shiftQueryWrapper.eq("shift_id", item.getShiftId());
                    schedulingVO.setShiftVO(BeanUtil.copyProperties(shiftMapper.selectOneAll(shiftQueryWrapper), ShiftVO.class));
                    schedulingVO.setUserVO(BeanUtil.copyProperties(userMapper.selectById(item.getUserId()), UserVO.class));
                    return schedulingVO;
                })
                .collect(Collectors.toList());
        return collect;
    }
}
