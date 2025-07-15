package com.tangdeng.hssystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangdeng.hssystem.mapper.ShiftMapper;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftServiceImpl extends ServiceImpl<ShiftMapper, Shift> implements ShiftService {
    @Autowired
    ShiftMapper shiftMapper;

    @Override
    public List<Shift> getBySearch(Integer shiftId, String shiftName) {
        LambdaQueryWrapper<Shift> queryWrapper = Wrappers.lambdaQuery();

        if (shiftId != null) {
            queryWrapper.eq(Shift::getShiftId, shiftId);
        }

        if (shiftName != null && !shiftName.isEmpty()) {
            queryWrapper.like(Shift::getShiftName, shiftName);
        }

        return shiftMapper.selectList(queryWrapper);
    }

    @Override
    public PageBean<Shift> getPage(Integer pageNum, Integer pageSize, String deptId, Integer shiftId, String shiftName) {
        IPage page = new Page(pageNum, pageSize);
        LambdaQueryWrapper<Shift> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Shift::getDeptId, deptId);
        if (shiftId != null) {
            queryWrapper.eq(Shift::getShiftId, shiftId);
        }

        if (shiftName != null && !shiftName.isEmpty()) {
            queryWrapper.like(Shift::getShiftName, shiftName);
        }

        shiftMapper.selectPage(page, queryWrapper);


        return new PageBean<>(page.getPages(),page.getCurrent(),page.getSize(), page.getTotal(), page.getRecords());
    }

    @Override
    public Shift selectOneAll(Integer shiftId) {
        QueryWrapper<Shift> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shift_id",shiftId);
        Shift shift = shiftMapper.selectOneAll(queryWrapper);
        return shift;
    }

}
