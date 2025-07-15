package com.tangdeng.hssystem.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangdeng.hssystem.pojo.dto.DepartmentDTO;
import com.tangdeng.hssystem.pojo.dto.UserDTO;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.DepartmentVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.DepartmentService;
import com.tangdeng.hssystem.service.UserDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    UserDepartmentService userDepartmentService;

    @PostMapping
    public Result addOne(@RequestBody DepartmentDTO departmentDTO) {
        Department department = BeanUtil.copyProperties(departmentDTO, Department.class);
        Department byId = departmentService.selectOneAll(department.getDeptId());
        if(byId != null) {
            return Result.error("部门号已经存在");
        }
        departmentService.save(department);
        return Result.success();
    }

    @DeleteMapping("/{deptid}")
    public Result deleteOne(@PathVariable("deptid") String deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id",deptId);
        userDepartmentService.remove(queryWrapper);
        boolean b = departmentService.removeById(deptId);
        if (b) return Result.success();
        else return Result.error();
    }

    @PutMapping("/{deptid}")
    public Result updateOne(@PathVariable("deptid") String deptId, @RequestBody DepartmentDTO departmentDTO) {
        Department department = BeanUtil.copyProperties(departmentDTO, Department.class);
        boolean b = departmentService.updateById(department);
        if(b) {
            Department byId = departmentService.getById(deptId);
            DepartmentVO departmentVO = BeanUtil.copyProperties(byId, DepartmentVO.class);
            return Result.success(departmentVO);
        } else  {
            return Result.error();
        }
    }

    @GetMapping("/{deptid}")
    public Result getOne(@PathVariable("deptid") String deptId) {
        Department byId = departmentService.selectOneAll(deptId);
        if(byId == null) {
            return Result.error("排班科室已被删除");
        } else {
            DepartmentVO departmentVO = BeanUtil.copyProperties(byId, DepartmentVO.class);
            return Result.success(departmentVO,"科室信息获取成功");
        }
    }

    @GetMapping ("/pages")
    public Result getAllPages(@RequestParam(required = true) Integer pageNum, @
                                RequestParam(required = true) Integer pageSize,
                             @RequestParam(required = false) String deptId,
                              @RequestParam(required = false) String deptName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if(deptId != null) {
            queryWrapper.eq("dept_id", deptId);
        }
        if(deptName != null) {
            queryWrapper.eq("dept_name", deptName);
        }
        PageBean pages = departmentService.getPages(pageNum, pageSize, queryWrapper);
        return Result.success(pages);
    }

   @GetMapping()
    public Result getAll() {
       List<Department> list = departmentService.list();
       return Result.success(list);
    }

    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<String> deptIds) {
        boolean b = departmentService.removeByIds(deptIds);
        if (b) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

}
