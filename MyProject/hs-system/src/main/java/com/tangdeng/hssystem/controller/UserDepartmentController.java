package com.tangdeng.hssystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.User;
import com.tangdeng.hssystem.pojo.entity.UserDepartment;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.DepartmentService;
import com.tangdeng.hssystem.service.UserDepartmentService;
import com.tangdeng.hssystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userdept")
public class UserDepartmentController {
    @Autowired
    UserDepartmentService userDepartmentService;
    @Autowired
    UserService userService;
    @Autowired
    DepartmentService departmentService;

    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<UserDepartment> userdpts) {
        for (UserDepartment userdpt : userdpts) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("user_id", userdpt.getUserId());
            queryWrapper.eq("dept_id", userdpt.getDeptId());
            userDepartmentService.remove(queryWrapper);
        }
        return Result.success();
    }

    @PostMapping("/batch")
    public Result addBatch(@RequestBody List<UserDepartment> userdpts) {
        for (UserDepartment userdpt : userdpts) {
            userDepartmentService.save(userdpt);
        }
        return Result.success();
    }

    @GetMapping("/able/doctor")
    public Result ableDocBatch(@RequestParam(required = false) String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_type", 1);
        List<User> list = userService.list(queryWrapper);

        List<UserDepartment> list1 = userDepartmentService.list();

        List<User> list2 = new ArrayList<>();
        for (UserDepartment userDepartment : list1) {
            User byId = userService.getById(userDepartment.getUserId());
            list2.add(byId);
        }

        // 差集：不在该科室的医生
        List<User> result = list.stream()
                .filter(user -> list2.stream().noneMatch(u2 -> u2.getUserId().equals(user.getUserId())))
                .collect(Collectors.toList());

        // 如果 userName 不为空，继续筛选用于搜索
        if (userName != null && !userName.trim().isEmpty()) {
            String keyword = userName.trim().toLowerCase();
            result = result.stream()
                    .filter(user -> user.getUserName() != null && user.getUserName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        return Result.success(result);
    }

    @GetMapping("/able/nurse")
    public Result ableNurseBatch(@RequestParam(required = false) String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_type", 2);
        List<User> list = userService.list(queryWrapper);

        List<UserDepartment> list1 = userDepartmentService.list();

        List<User> list2 = new ArrayList<>();
        for (UserDepartment userDepartment : list1) {
            User byId = userService.getById(userDepartment.getUserId());
            list2.add(byId);
        }

        // 差集：不在该科室的医生
        List<User> result = list.stream()
                .filter(user -> list2.stream().noneMatch(u2 -> u2.getUserId().equals(user.getUserId())))
                .collect(Collectors.toList());

        // 如果 userName 不为空，继续筛选用于搜索
        if (userName != null && !userName.trim().isEmpty()) {
            String keyword = userName.trim().toLowerCase();
            result = result.stream()
                    .filter(user -> user.getUserName() != null && user.getUserName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }

        return Result.success(result);
    }

    @GetMapping("/dept/{userid}")
    public Result deptUser (@PathVariable("userid") String userid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userid);

        List<UserDepartment> list = userDepartmentService.list(queryWrapper);
        if(list.size() == 0) {
            return Result.error("未绑定科室");
        } else {
            UserDepartment userDepartment = list.get(0);
            Department byId = departmentService.getById(userDepartment.getDeptId());
            return Result.success(byId);
        }

    }

}
