package com.tangdeng.hssystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tangdeng.hssystem.pojo.dto.LeaveAddDTO;
import com.tangdeng.hssystem.pojo.dto.LeaveBatchDTO;
import com.tangdeng.hssystem.pojo.entity.Leave;
import com.tangdeng.hssystem.pojo.entity.Scheduling;
import com.tangdeng.hssystem.pojo.entity.User;
import com.tangdeng.hssystem.pojo.entity.UserDepartment;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.LeaveService;
import com.tangdeng.hssystem.service.SchedulingService;
import com.tangdeng.hssystem.service.UserDepartmentService;
import com.tangdeng.hssystem.service.UserService;
import com.tangdeng.hssystem.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    LeaveService leaveService;
    @Autowired
    SchedulingService schedulingService;
    @Autowired
    MailUtils mailUtils;
    @Autowired
    UserService userService;
    @Autowired
    UserDepartmentService userDepartmentService;


    @GetMapping
    public Result getAll(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                        @RequestParam(required = false) Integer leaveId,
                         @RequestParam(required = false) String userId,
                         @RequestParam(required = false) Integer leaveStatus,
                         @RequestParam(required = false) Boolean isPast,
                         @RequestParam String deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id", deptId);

        // 获取当前时间并格式化为 YYYY-MM-DD 23:59:59
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sdf.format(new Date());

        if (leaveId != null) {
            queryWrapper.eq("leave_id", leaveId);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (leaveStatus != null) {
            queryWrapper.eq("leave_status", leaveStatus);
        }
        if (isPast == null) {
            queryWrapper.ge("leave_date", nowStr);
        } else {
            queryWrapper.le("leave_date", nowStr);
        }

        PageBean pages = leaveService.getPages(pageNum, pageSize, queryWrapper);

        return Result.success(pages,"数据获取成功");
    }

    @PutMapping("/{leaveId}")
    public Result audit(@PathVariable Integer leaveId,@RequestBody Boolean auditOperator) {
        Leave leaveOne = leaveService.getById(leaveId);
        Scheduling scheOne = schedulingService.getById(leaveOne.getScheId());
        User user = userService.getById(leaveOne.getUserId());

        if(auditOperator) {
            leaveOne.setLeaveStatus(2);
            scheOne.setScheStatus(1);
            leaveService.updateById(leaveOne);
            schedulingService.updateById(scheOne);
            UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("user_id", null).eq("sche_id", scheOne.getScheId());
            schedulingService.update(updateWrapper);
// 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

            String leaveDateStr = leaveOne.getLeaveDate() != null ? sdf.format(leaveOne.getLeaveDate()) : "未知日期";
            String scheDateStr = scheOne.getScheDate() != null ? sdf.format(scheOne.getScheDate()) : "未知日期";

// 邮件正文
            String content = String.format(
                    "尊敬的 %s：\n\n" +
                            "您于 %s 提交的请假申请已审核通过，系统已为您做出相应调整。\n" +
                            "- 请假日期：%s\n" +
                            "- 原排班内容：%s\n\n" +
                            "请您合理安排个人事务，并注意及时返回工作岗位。\n" +
                            "如有后续调整需求，请及时联系科室负责人。\n\n" +
                            "祝您一切顺利！\n\n" +
                            "—— 医院排班系统",
                    user.getUserName(),
                    leaveDateStr,
                    scheDateStr,
                    scheOne.getScheInfo() != null ? scheOne.getScheInfo() : "无"
            );

// 发送邮件
            mailUtils.sendTextMailMessage(user.getUserEmail(), "请假申请审核通过", content);

        } else {
            scheOne.setUserId(leaveOne.getUserId());
            leaveOne.setLeaveStatus(3);
            scheOne.setScheStatus(2);
            leaveService.updateById(leaveOne);
            schedulingService.updateById(scheOne);
            // 格式化日期（可选）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

            String leaveDateStr = leaveOne.getLeaveDate() != null ? sdf.format(leaveOne.getLeaveDate()) : "未知日期";
            String scheDateStr = scheOne.getScheDate() != null ? sdf.format(scheOne.getScheDate()) : "未知日期";

// 构造邮件正文
            new Thread(() -> {
                String content = String.format(
                        "尊敬的 %s：\n\n" +
                                "您于 %s 提交的请假申请未能通过审核，具体信息如下：\n" +
                                "- 排班日期：%s\n" +
                                "- 排班内容：%s\n\n" +
                                "请您根据工作安排合理调整，如有疑问可联系科室负责人。\n\n" +
                                "感谢您的理解与配合！\n\n" +
                                "—— 医院排班系统",
                        user.getUserName(),
                        leaveDateStr,
                        scheDateStr,
                        scheOne.getScheInfo() != null ? scheOne.getScheInfo() : "无"
                );

// 发送邮件
                mailUtils.sendTextMailMessage(user.getUserEmail(), "请假审核结果通知", content);
            }).start(); // 启动线程


        }
        return Result.success();
    }

    @PostMapping()
    public Result addleave(@RequestBody LeaveAddDTO leaveAddDTO) {
        Scheduling scheduling = schedulingService.getById(leaveAddDTO.getScheId());
        String deptId = scheduling.getDeptId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id", deptId);
        List<UserDepartment> list = userDepartmentService.list(queryWrapper);
        List<User> userList = new LinkedList<>();
        for (UserDepartment userDepartment : list) {
            User byId = userService.getById(userDepartment.getUserId());
            if(byId.getUserPermission() >= 2) {
                userList.add(byId);
            }
        }
        User applicant = userService.getById(scheduling.getUserId());

        scheduling.setScheStatus(3);
        schedulingService.updateById(scheduling);
        //修改scheduling;
        Leave leave = new Leave();
        leave.setLeaveStatus(1);
        leave.setLeaveDate(scheduling.getScheDate());
        leave.setUserId(scheduling.getUserId());
        leave.setDeptId(scheduling.getDeptId());
        leave.setScheId(leaveAddDTO.getScheId());
        leave.setLeaveInfo(leaveAddDTO.getLeaveInfo());
        leave.setIsDeleted(0);

        leaveService.save(leave);

        new Thread(() -> {
            for (User user : userList) {
                String subject = "您有一条请假申请信息";
                String content = "尊敬的" + user.getUserName() + "，您好：\n\n"
                        + "医护人员【" + applicant.getUserName() + "】提交了一条新的请假申请，请您及时登录系统查看并处理。\n\n"
                        + "如有疑问，请联系相关工作人员。\n\n"
                        + "此致\n敬礼！\n\n"
                        + "医院排班系统";
                mailUtils.sendTextMailMessage(user.getUserEmail(), subject, content);
            }
        }).start(); // 启动线程

        return Result.success();

    }

    @PutMapping("/batch")
    public Result auditBatch(@RequestBody LeaveBatchDTO leaveBatchDTO) {
        for (Integer leaveId :leaveBatchDTO.getLeaveIds()) {
            Leave leaveOne = leaveService.getById(leaveId);
            Scheduling scheOne = schedulingService.getById(leaveOne.getScheId());
            if(leaveBatchDTO.getAuditOperator()) {
                leaveOne.setLeaveStatus(2);
                scheOne.setScheStatus(1);
                leaveService.updateById(leaveOne);
                schedulingService.updateById(scheOne);
                UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("user_id", null).eq("sche_id", scheOne.getScheId());
                schedulingService.update(updateWrapper);

            } else {
                scheOne.setUserId(leaveOne.getUserId());
                leaveOne.setLeaveStatus(3);
                scheOne.setScheStatus(2);
                leaveService.updateById(leaveOne);
                schedulingService.updateById(scheOne);
            }
        }
        return Result.success();
    }
    @DeleteMapping("/{leaveid}")
    public Result deleteOne(@PathVariable("leaveid") String leaveid){
        Leave leave = leaveService.getById(leaveid);
        Integer scheId = leave.getScheId();
        Scheduling scheduling = schedulingService.getById(scheId);
        scheduling.setScheStatus(1);
        schedulingService.updateById(scheduling);

        leaveService.removeById(leaveid);
        return Result.success();
    }
}

