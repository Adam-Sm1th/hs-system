package com.tangdeng.hssystem.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangdeng.hssystem.pojo.dto.ShiftDTO;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.ShiftVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shift")
public class ShiftController {
    @Autowired
    ShiftService shiftService;

    @GetMapping("/{shiftid}")
    public Result getOne (@PathVariable("shiftid") Integer shiftid) {
        Shift byId = shiftService.getById(shiftid);
        ShiftVO shiftVO = BeanUtil.copyProperties(byId, ShiftVO.class);
        if(byId == null) {
            return Result.error();
        } else {
            return Result.success(shiftVO);
        }
    }

    @GetMapping("/search")
    public Result getBySearch(@RequestParam(required = false) Integer shiftId,
                              @RequestParam(required = false) String shiftName) {
        List<Shift> shifts = shiftService.getBySearch(shiftId, shiftName);
        if (shifts.isEmpty()) {
            return Result.error("没有找到符合条件的班次");
        }
        List<ShiftVO> collect = shifts.stream()
                .map(shift -> BeanUtil.copyProperties(shift, ShiftVO.class))
                .collect(Collectors.toList());
        return Result.success(collect);
    }


    @GetMapping("/pages")
    public Result getAllPages(@RequestParam Integer pageNum, @RequestParam Integer pageSize,@RequestParam String deptId,
                         @RequestParam(required = false) Integer shiftId,
                         @RequestParam(required = false) String shiftName) {
        PageBean<Shift> page = shiftService.getPage(pageNum, pageSize, deptId, shiftId, shiftName);

        if (page == null) {
            return Result.error("服务器错误");
        } else {
            return Result.success(page,"数据获取成功");
        }
    }

    @GetMapping
    public Result getAll(@RequestParam(required = false) String deptId) {
        if(deptId != null) {
            QueryWrapper<Shift> shiftQueryWrapper = new QueryWrapper<>();
            shiftQueryWrapper.eq("dept_id", deptId);
            List<Shift> list = shiftService.list(shiftQueryWrapper);
            List<ShiftVO> collect = list.stream().map(item -> BeanUtil.copyProperties(item, ShiftVO.class)).collect(Collectors.toList());
            return Result.success(collect,"班次列表获取成功");
        } else {
            List<Shift> list = shiftService.list();
            List<ShiftVO> collect = list.stream().map(item -> BeanUtil.copyProperties(item, ShiftVO.class)).collect(Collectors.toList());
            return Result.success(collect,"班次列表获取成功");
        }

    }

    @PostMapping
    public Result addOne(@RequestBody ShiftDTO shiftDTO) {
        Shift shift = BeanUtil.copyProperties(shiftDTO, Shift.class);
        boolean save = shiftService.save(shift);
        if(save) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    @PutMapping("/{shiftid}")
    public Result updateOne(@PathVariable int shiftid, @RequestBody ShiftDTO shiftDTO) {
        Shift shift = BeanUtil.copyProperties(shiftDTO, Shift.class);
        boolean b = shiftService.updateById(shift);
        if(b) {
            Shift byId = shiftService.getById(shiftid);
            ShiftVO shiftVO = BeanUtil.copyProperties(byId, ShiftVO.class);
            return Result.success(shiftVO);
        } else {
            return Result.error();
        }
    }

    @DeleteMapping("/{shiftid}")
    public Result deleteOne(@PathVariable int shiftid) {
        boolean b = shiftService.removeById(shiftid);
        if(b) {
            List<Shift> shifts = shiftService.list();
            List<ShiftVO> shiftVOs = shifts.stream()
                    .map(shift -> BeanUtil.copyProperties(shift, ShiftVO.class))
                    .collect(Collectors.toList());
            return Result.success(shiftVOs);
        } else {
            return Result.error();
        }
    }

    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Integer> shiftIds) {
        boolean b = shiftService.removeByIds(shiftIds);

        if (b) {
            List<Shift> shifts = shiftService.list();
            List<ShiftVO> shiftVOs = shifts.stream()
                    .map(shift -> BeanUtil.copyProperties(shift, ShiftVO.class))
                    .collect(Collectors.toList());

            return Result.success(shiftVOs);
        } else {
            return Result.error();
        }
    }


}
