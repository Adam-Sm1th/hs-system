package com.tangdeng.hssystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;

import java.util.List;

public interface ShiftService extends IService<Shift> {
    List<Shift> getBySearch(Integer shiftId, String shiftName);
    PageBean<Shift> getPage(Integer pageNum,Integer pageSize,String deptId,Integer shiftId, String shiftName);
    Shift selectOneAll(Integer shiftId);
}
