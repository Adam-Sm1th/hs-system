package com.tangdeng.hssystem.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tangdeng.hssystem.pojo.dto.*;
import com.tangdeng.hssystem.pojo.entity.*;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.CshiftVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.*;
import com.tangdeng.hssystem.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cshift")
public class CshiftController {
    @Autowired
    CshiftService cshiftService;
    @Autowired
    SchedulingService schedulingService;
    @Autowired
    ShiftService shiftService;
    @Autowired
    UserService userService;
    @Autowired
    MailUtils mailUtils;
    @Autowired
    UserDepartmentService userDepartmentService;


    @GetMapping("/{cshiftid}")
    public Result getOne(@PathVariable("cshiftid") String cshiftId) {
        Cshift byId = cshiftService.getById(cshiftId);
        if(byId == null) {
            return Result.error();
        } else {
            CshiftVO cshiftVO = BeanUtil.copyProperties(byId, CshiftVO.class);
            return Result.success(cshiftVO,"获取成功");
        }
    }

//    @PostMapping
//    public Result addOne(@RequestBody CshiftDTO cshiftDTO) {
//        Cshift cshift = BeanUtil.copyProperties(cshiftDTO, Cshift.class);
//        Cshift byId = cshiftService.getById(cshift.getCshiftId());
//        if(byId != null) {
//            return Result.error("已存在");
//        }
//        cshiftService.save(cshift);
//        return Result.success();
//    }

    @GetMapping
    public Result getAll(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                         @RequestParam(required = false) Integer cshiftId,
                         @RequestParam(required = false) String userId,
                         @RequestParam(required = false) Integer cshiftStatus,
                         @RequestParam(required = false) Boolean isPast,
                         @RequestParam String deptId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("dept_id", deptId);

        // 获取当前时间并格式化为 YYYY-MM-DD 23:59:59
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowStr = sdf.format(new Date());

        if (cshiftId != null) {
            queryWrapper.eq("cshift_id", cshiftId);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (cshiftStatus != null) {
            queryWrapper.eq("cshift_status", cshiftStatus);
        }
        if (isPast == null) {
            queryWrapper.ge("cshift_original_day", nowStr);
            queryWrapper.ge("cshift_target_day", nowStr);
        } else {
            if (isPast == null) {
                queryWrapper.ge("cshift_original_day", nowStr);
                queryWrapper.ge("cshift_target_day", nowStr);
            } else {
                queryWrapper.and(wq -> ((QueryWrapper<Cshift>) wq)
                        .le("cshift_original_day", nowStr)
                        .or()
                        .le("cshift_target_day", nowStr)
                );
            }
        }

        PageBean pages = cshiftService.getPages(pageNum, pageSize, queryWrapper);
        return Result.success(pages,"数据获取成功");
    }

    @PutMapping("/delay/{cshiftid}")
    public Result auditDelay(@PathVariable Integer cshiftid) {
        Cshift cshift = cshiftService.getById(cshiftid);
        Scheduling scheOne = schedulingService.getById(cshift.getScheOriginalId());
        User user = userService.getById(cshift.getUserId());
        Shift shift = shiftService.getById(cshift.getShiftTargetId());
        cshift.setCshiftStatus(3);
        scheOne.setScheStatus(2);
        cshiftService.updateById(cshift);
        schedulingService.updateById(scheOne);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String originalDay = cshift.getCshiftOriginalDay() != null ? sdf.format(cshift.getCshiftOriginalDay()) : "未知日期";
        String targetDay = cshift.getCshiftTargetDay() != null ? sdf.format(cshift.getCshiftTargetDay()) : "未知日期";

// 邮件正文
        String content = String.format(
                "尊敬的 %s：\n\n" +
                        "您提交的调班申请暂未通过审核，相关排班未做调整。\n" +
                        "- 原排班日期：%s\n" +
                        "- 拟调班日期：%s\n" +
                        "- 拟调班班次：%s\n" +
                        "- 调班说明：%s\n\n" +
                        "请您继续按照原排班安排出勤，若需再次申请或协商调班，请联系科室负责人。\n\n" +
                        "感谢您的理解与配合！\n\n" +
                        "—— 医院排班系统",
                user.getUserName(),
                originalDay,
                targetDay,
                shift.getShiftName(),
                (cshift.getCshiftInfo() == null || cshift.getCshiftInfo().trim().isEmpty()) ? "无" : cshift.getCshiftInfo()

        );

        mailUtils.sendTextMailMessage(user.getUserEmail(), "调班申请未通过", content);

        return Result.success();
//        if(auditOperator) {
//            leaveOne.setLeaveStatus(2);
//            scheOne.setScheStatus(1);
//            leaveService.updateById(leaveOne);
//            schedulingService.updateById(scheOne);
//            UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.set("user_id", null).eq("sche_id", scheOne.getScheId());
//            schedulingService.update(updateWrapper);
//
//        } else {
//            scheOne.setUserId(leaveOne.getUserId());
//            leaveOne.setLeaveStatus(3);
//            scheOne.setScheStatus(2);
//            leaveService.updateById(leaveOne);
//            schedulingService.updateById(scheOne);
//        }
    }

    @PutMapping("/accept/{cshiftid}")
    public Result auditAccept(@PathVariable Integer cshiftid,@RequestBody CshiftDTO cshiftDTO) {
        User user = userService.getById(cshiftDTO.getUserId());
        Integer scheTargetId = cshiftDTO.getScheTargetId();
        Integer scheOriginalId = cshiftDTO.getScheOriginalId();
        Scheduling tarScheduling = schedulingService.getById(scheTargetId);
        Scheduling orgScheduling = schedulingService.getById(scheOriginalId);
        Cshift cshift = cshiftService.getById(cshiftid);
        Shift shift = shiftService.getById(cshift.getShiftTargetId());
        cshiftDTO.setCshiftStatus(2);
        cshiftService.updateById(BeanUtil.copyProperties(cshiftDTO, Cshift.class));

        if(tarScheduling.getScheStatus() == 1) {
            UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("sche_status", 1).eq("sche_id", orgScheduling.getScheId());
            updateWrapper.set("user_id", null).eq("sche_id", orgScheduling.getScheId());
            schedulingService.update(updateWrapper); //旧的排班设置为空

            tarScheduling.setUserId(cshiftDTO.getUserId());
            tarScheduling.setScheStatus(2);
            schedulingService.updateById(tarScheduling); //新的排班设置好
        } else { //交换排班的情况
            String userId = tarScheduling.getUserId();
            tarScheduling.setUserId(orgScheduling.getUserId());
            orgScheduling.setUserId(userId);
            schedulingService.updateById(orgScheduling);
            schedulingService.updateById(tarScheduling);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

        String originalDay = cshiftDTO.getCshiftOriginalDay() != null ? sdf.format(cshiftDTO.getCshiftOriginalDay()) : "未知日期";
        String targetDay = cshiftDTO.getCshiftTargetDay() != null ? sdf.format(cshiftDTO.getCshiftTargetDay()) : "未知日期";

// 邮件正文
        new Thread(() -> {
            String content = String.format(
                    "尊敬的 %s：\n\n" +
                            "您提交的调班申请已审核通过，系统已完成相关排班调整。\n" +
                            "- 原排班日期：%s\n" +
                            "- 目标排班日期：%s\n" +
                            "- 拟调班班次：%s\n" +
                            "- 调班说明：%s\n\n" +
                            "请您及时查看并确认最新排班信息，确保按时出勤。\n" +
                            "如有疑问，请及时联系部门负责人。\n\n" +
                            "祝工作顺利，身体健康！\n\n" +
                            "—— 医院排班系统",
                    user.getUserName(),
                    originalDay,
                    targetDay,
                    shift.getShiftName(),
                    (cshift.getCshiftInfo() == null || cshift.getCshiftInfo().trim().isEmpty()) ? "无" : cshift.getCshiftInfo()
            );
            mailUtils.sendTextMailMessage(user.getUserEmail(), "调班申请审核通过", content);
        }).start(); // 启动线程


        return Result.success();
    }

    @GetMapping("/suitdata/{cshiftid}")
    public Result suitData(@PathVariable Integer cshiftid) {
        Cshift cshift = cshiftService.getById(cshiftid);
        Date cshiftTargetDay = cshift.getCshiftTargetDay();
        Integer scheOriginalId = cshift.getScheOriginalId();
        Scheduling scheduling = schedulingService.getById(scheOriginalId);
        Integer userType = scheduling.getUserType();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("sche_date", cshiftTargetDay);
        queryWrapper.in("sche_status",1,2);
        queryWrapper.eq("shift_id", cshift.getShiftTargetId());
        queryWrapper.eq("user_type", userType);

        List<Scheduling> list = schedulingService.list(queryWrapper);


        Iterator<Scheduling> iterator = list.iterator();
        while (iterator.hasNext()) {
            Scheduling sc = iterator.next();
            if (!checkUserSche(sc.getScheId(), cshift.getUserId()) || !checkUserSche(scheOriginalId, sc.getUserId())) {
                iterator.remove();  // 使用迭代器的 remove() 方法，避免并发修改异常
            }
        }


        return Result.success(list);
    }

    @PutMapping("/batch")
    public Result auditBatch(@RequestBody CshiftBatchDTO cshiftBatchDTO) {
        for (Integer cshiftId : cshiftBatchDTO.getCshiftIds()) {
            Cshift cshift = cshiftService.getById(cshiftId);
            Scheduling scheOne = schedulingService.getById(cshift.getScheOriginalId());

            cshift.setCshiftStatus(3);
            scheOne.setScheStatus(2);
            cshiftService.updateById(cshift);
            schedulingService.updateById(scheOne);
        }
        return Result.success();
    }

    @PostMapping()
    public Result addCshift(@RequestBody CshiftAddDTO cshiftAddDTO) {
        // 获取当前排班信息
        Scheduling scheduling = schedulingService.getById(cshiftAddDTO.getScheId());
        if (scheduling == null) {
            return Result.error("未找到对应的排班信息");
        }

        // 更新排班状态为调班申请中
        scheduling.setScheStatus(4);
        schedulingService.updateById(scheduling);

        // 构建调班申请信息
        Cshift cshift = new Cshift();
        cshift.setUserId(scheduling.getUserId());
        cshift.setScheOriginalId(scheduling.getScheId());
        cshift.setCshiftInfo(cshiftAddDTO.getCshiftInfo());
        cshift.setCshiftTargetDay(cshiftAddDTO.getCshiftTargetDay());
        cshift.setShiftTargetId(cshiftAddDTO.getShiftTargetId());
        cshift.setCshiftStatus(1);
        cshift.setCshiftOriginalDay(scheduling.getScheDate());
        cshift.setDeptId(scheduling.getDeptId());
        cshift.setIsDeleted(0);

        cshiftService.save(cshift);

        // 获取申请人信息
        User applicant = userService.getById(scheduling.getUserId());

        // 获取本部门的用户关系记录
        QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dept_id", scheduling.getDeptId());
        List<UserDepartment> userDeptList = userDepartmentService.list(queryWrapper);

        // 批量获取用户ID并查询用户信息
        List<String> userIds = userDeptList.stream()
                .map(UserDepartment::getUserId)
                .collect(Collectors.toList());
        List<User> allUsers = userService.listByIds(userIds);

        // 过滤出有权限的用户（权限 >= 2）
        List<User> userList = allUsers.stream()
                .filter(user -> user.getUserPermission() != null && user.getUserPermission() >= 2)
                .collect(Collectors.toList());

        // 构造邮件内容
        String subject = "您有一条调班申请信息";
        String originalDate = String.valueOf(scheduling.getScheDate());
        String targetDate = String.valueOf(cshiftAddDTO.getCshiftTargetDay());
        String applicantName = applicant != null ? applicant.getUserName() : "未知用户";

        for (User user : userList) {
            new Thread(() -> {
                String content = "尊敬的" + user.getUserName() + "，您好：\n\n"
                        + "医护人员【" + applicantName + "】提交了一条调班申请，"
                        + "原排班日期为：" + originalDate + "，"
                        + "目标调班日期为：" + targetDate + "。\n\n"
                        + "请您及时登录系统查看并处理。\n\n"
                        + "如有疑问，请联系相关工作人员。\n\n"
                        + "此致\n敬礼！\n\n"
                        + "医院排班系统";

                mailUtils.sendTextMailMessage(user.getUserEmail(), subject, content);
            }).start(); // 启动线程
        }


        return Result.success();
    }



    @DeleteMapping("/{cshiftid}")
    public Result deleteOne(@PathVariable("cshiftid") String cshiftid){
        Cshift cshift = cshiftService.getById(cshiftid);
        Integer scheId = cshift.getScheOriginalId();
        Scheduling scheduling = schedulingService.getById(scheId);
        scheduling.setScheStatus(1);
        schedulingService.updateById(scheduling);

        cshiftService.removeById(cshiftid);
        return Result.success();
    }
    public boolean checkUserSche(Integer scheId, String userId) {
        Scheduling scheduling = schedulingService.getById(scheId);
        Shift myshift = shiftService.selectOneAll(scheduling.getShiftId());

        LocalTime shiftBegintime = myshift.getShiftBegintime();
        LocalTime shiftEndtime = myshift.getShiftEndtime();
        Date scheDate = scheduling.getScheDate();

        List<Scheduling> schedulingsByid = schedulingService.getByUserId(userId, scheDate);

        for (Scheduling sc : schedulingsByid) {
            Integer shiftId = sc.getShiftId();
            Shift shift = shiftService.selectOneAll(shiftId);
            LocalTime scheShiftBegintime = shift.getShiftBegintime();
            LocalTime scheShiftEndtime = shift.getShiftEndtime();

            if (scheShiftBegintime.isBefore(shiftEndtime) && shiftBegintime.isBefore(scheShiftEndtime) && sc.getUserId() != userId) {
                return false; // 存在时间冲突
            }
        }
        return true;
    }


}
