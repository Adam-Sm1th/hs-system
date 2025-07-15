package com.tangdeng.hssystem.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tangdeng.hssystem.pojo.dto.SchedulingDTO;
import com.tangdeng.hssystem.pojo.dto.ShiftDTO;
import com.tangdeng.hssystem.pojo.entity.*;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.EchartVO;
import com.tangdeng.hssystem.pojo.vo.SchedulingVO;
import com.tangdeng.hssystem.pojo.vo.ShiftVO;
import com.tangdeng.hssystem.pojo.vo.UserVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.*;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sche")
public class SchedulingController {
    @Autowired
    SchedulingService schedulingService;
    @Autowired
    ShiftService shiftService;
    @Autowired
    UserService userService;
    @Autowired
    LeaveService leaveService;
    @Autowired
    UserDepartmentService userDepartmentService;

    @GetMapping("/{scheid}")
    public Result getOne(@PathVariable("scheid") Integer scheid) {
        Scheduling byId = schedulingService.getById(scheid);
        SchedulingVO schedulingVO = BeanUtil.copyProperties(byId, SchedulingVO.class);
        schedulingVO.setShiftVO(BeanUtil.copyProperties(shiftService.getById(schedulingVO.getShiftId()), ShiftVO.class));
        schedulingVO.setUserVO(BeanUtil.copyProperties(userService.getById(schedulingVO.getUserId()), UserVO.class));
        return Result.success(schedulingVO);
    }

    @GetMapping
    public Result getAll(@RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String deptId,
                         @RequestParam(required = false) Integer scheId,
                         @RequestParam(required = false) String scheName,
                         @RequestParam(required = false) String scheInfo,
                         @RequestParam(required = false) Date beginDate,
                         @RequestParam(required = false) Date endDate,
                         @RequestParam(required = false) Integer shiftId,
                         @RequestParam(required = false) String userId,
                         @RequestParam(required = false) Integer userType,
                         @RequestParam(required = false) Integer scheStatus,
                         @RequestParam(required = false) Boolean isPast) {

        QueryWrapper<Scheduling> schedulingQueryWrapper = new QueryWrapper<>();
        if(deptId != null) {
            schedulingQueryWrapper.eq("dept_id", deptId);
        }

        if (scheId != null) {
            schedulingQueryWrapper.eq("sche_id", scheId);
        }
        if (scheName != null) {
            schedulingQueryWrapper.like("sche_name", scheName);
        }
        if (scheInfo != null) {
            schedulingQueryWrapper.like("sche_info", scheInfo);
        }
        if (beginDate != null) {
            Date now = new Date();
            if(isPast == null) {
                if(now.after(beginDate)) {
                    schedulingQueryWrapper.ge("sche_date", now);
                } else {
                    schedulingQueryWrapper.ge("sche_date", beginDate);
                }
            } else {
                schedulingQueryWrapper.ge("sche_date", beginDate);
            }

        } else {
            if(isPast == null) {
                schedulingQueryWrapper.ge("sche_date", new Date());
            }
        }
        if (endDate != null) {
            schedulingQueryWrapper.le("sche_date", endDate);  // 小于等于endDate
        }
        if (shiftId != null) {
            schedulingQueryWrapper.eq("shift_id", shiftId);
        }
        if (userId != null) {
            schedulingQueryWrapper.eq("user_id", userId);
        }
        if (userType != null) {
            schedulingQueryWrapper.eq("user_type", userType);
        }
        if (scheStatus != null) {
            schedulingQueryWrapper.eq("sche_status", scheStatus);
        }

        if (isPast != null) {
            if (isPast) {
            } else {
                schedulingQueryWrapper.ge("sche_date", new Date());
            }
        }

        if(pageNum != null) {
            PageBean pages = schedulingService.getPages(pageNum, pageSize, schedulingQueryWrapper);
            return Result.success(pages,"排班数据获取成功");
        } else {
            List<SchedulingVO> allByUserId = schedulingService.getAllByUserId(userId,deptId);
            return Result.success(allByUserId, "排班数据获取成功");
        }
        // 获取分页数据

    }

    @DeleteMapping("/{scheid}")
    public Result deleteOne(@PathVariable int scheid) {
        boolean b = schedulingService.removeById(scheid);
        QueryWrapper<Leave> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sche_id", scheid);
        Leave one = leaveService.getOne(queryWrapper);
        if(one != null) {
            leaveService.removeById(one.getLeaveId());
        }
        if(b) return Result.success();
        else return Result.error();
    }

    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<Integer> schedulingIds) {
        boolean b = schedulingService.removeByIds(schedulingIds);
        if (b) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    @PostMapping
    public Result addOne(@RequestBody SchedulingDTO schedulingDTO) {
        for (Integer i = 0; i < schedulingDTO.getUserNum(); i++) {
            Scheduling scheduling1 = BeanUtil.copyProperties(schedulingDTO, Scheduling.class);
            schedulingService.save(scheduling1);
        }

        return Result.success("新增成功");

    }

    @PutMapping("/{scheid}")
    public Result updateOne(@PathVariable int scheid, @RequestBody SchedulingDTO schedulingDTO) {
        Scheduling scheduling = BeanUtil.copyProperties(schedulingDTO, Scheduling.class);
        Scheduling byId = schedulingService.getById(scheduling.getScheId());
        if(byId.getUserId() != null && (byId.getScheStatus() == 3 || byId.getScheStatus() == 4)) {
            return Result.error("该排班在审核处理中，无法更改排班人员信息");
        }
        if(((byId.getUserId() == null && scheduling.getUserId() != null) || (scheduling.getUserId() != null)  && !byId.getUserId().equals(scheduling.getUserId()))) {
            boolean b = checkUserSche(scheduling.getScheId(), scheduling.getUserId());
            if(!b) {
                return Result.error("存在时间冲突");
            } else {
                scheduling.setScheStatus(2);
            }
        }
        boolean b2 = schedulingService.updateById(scheduling);
        if(b2) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    @PutMapping("/deluser/{scheid}")
    public Result delete(@PathVariable int scheid) {
        UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("user_id", null).eq("sche_id", scheid);
        updateWrapper.set("sche_status", 1).eq("sche_id", scheid);
        schedulingService.update(updateWrapper);
        return Result.success();
    }

    @PutMapping("/delscheuser")
    public Result delUser(@RequestBody SchedulingDTO schedulingDTO) {
        Scheduling scheduling = BeanUtil.copyProperties(schedulingDTO, Scheduling.class);
        Scheduling byId = schedulingService.getById(scheduling.getScheId());
        if(byId.getUserId() != null && (byId.getScheStatus() == 3 || byId.getScheStatus() == 4)) {
            return Result.error("该排班在审核处理中，无法更改排班人员信息");
        }
        UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("user_id", null).eq("sche_id", scheduling.getScheId());
        updateWrapper.set("sche_status", 1).eq("sche_id", scheduling.getScheId());
        schedulingService.update(updateWrapper);

        return Result.success();

    }

    @PutMapping("/addscheuser")
    public Result addScheUser(@RequestBody SchedulingDTO schedulingDTO) {
        Scheduling scheduling = BeanUtil.copyProperties(schedulingDTO, Scheduling.class);
        Scheduling byId = schedulingService.getById(scheduling.getScheId());
        if(byId.getUserId() != null && (byId.getScheStatus() == 3 || byId.getScheStatus() == 4)) {
            return Result.error("该排班在审核处理中，无法更改排班人员信息");
        }
        boolean b = checkUserSche(scheduling.getScheId(), scheduling.getUserId());
        if(!b) {
            return Result.error("存在时间冲突");
        } else {
            scheduling.setScheStatus(2);
        }
        boolean b2 = schedulingService.updateById(scheduling);
        if(b2) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    @GetMapping("/enableSche")
    public Result enableSche(@RequestParam String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date scheDate) {
        User byId = userService.getById(userId);//用户信息
        Integer userType = byId.getUserType();
        QueryWrapper q = new QueryWrapper();
        LocalDate localDate = scheDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        q.eq("user_type", userType);
        q.eq("sche_date", localDate);
        q.eq("sche_status", 1);
        List<Scheduling> list = schedulingService.list(q);
        List<Scheduling> ans = new LinkedList<>();

        for (Scheduling scheduling : list) {
            if(checkUserSche(scheduling.getScheId(), userId)) {
                ans.add(scheduling);
            }
        }
        Set<Scheduling> uniqueSchedulingSet = new HashSet<>(ans);
        ans.clear();
        ans.addAll(uniqueSchedulingSet);

        List<SchedulingVO> scheVOList = schedulingService.getScheVOList(ans);
        return Result.success(scheVOList);
    }

    @GetMapping("/scheStatus")
    public Result scheStatus(@RequestParam String deptId) {
        QueryWrapper shiftWrapper = new QueryWrapper();
        shiftWrapper.eq("dept_id", deptId);
        List<Shift> shiftList = shiftService.list(shiftWrapper); // 查询班次

        List<String> seriesNames = new LinkedList<>();
        seriesNames.add("全部");
        // 获取当前日期和一个月前的日期
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        // 构建最近一个月的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = oneMonthAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateList.add(date);
        }

        List<List> yAxis = new LinkedList<>();
        List<Integer> sumDates = new LinkedList<>();
        for (LocalDate localDate : dateList) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("sche_date",localDate);
            queryWrapper.eq("dept_id", deptId);
            List<Scheduling> schedulings = schedulingService.list(queryWrapper);
            sumDates.add(schedulings.size());
        }
        yAxis.add(sumDates);

        for (Shift shift : shiftList) {
            List<Integer> list = new LinkedList<>();
            for (LocalDate localDate : dateList) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("sche_date",localDate);
                queryWrapper.eq("dept_id", deptId);
                queryWrapper.eq("shift_id", shift.getShiftId());
                List<Scheduling> schedulings = schedulingService.list(queryWrapper);
                list.add(schedulings.size());
            }
            seriesNames.add(shift.getShiftName());
            yAxis.add(list);
        }


        EchartVO echartVO = new EchartVO();
        echartVO.setXAxis(dateList);
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一个月排班记录统计");
        echartVO.setSeriesNames(seriesNames);

        return Result.success(echartVO);
    }

    @GetMapping("/schePieStatus")
    public Result schePieStatus(@RequestParam String deptId) {
        QueryWrapper shiftWrapper = new QueryWrapper();
        shiftWrapper.eq("dept_id", deptId);
        List<Shift> shiftList = shiftService.list(shiftWrapper); // 查询班次
        List<String> seriesNames = new LinkedList<>();

        List<Integer> list = new LinkedList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        for (Shift shift : shiftList) {
            seriesNames.add(shift.getShiftName());
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("dept_id", deptId);
            queryWrapper.eq("shift_id", shift.getShiftId());
            queryWrapper.ge("sche_date",oneMonthAgo);
            queryWrapper.le("sche_date", today);
            List list1 = schedulingService.list(queryWrapper);
            list.add(list1.size());
        }
        List<List> yAxis = new LinkedList<>();
        yAxis.add(list);

        EchartVO echartVO = new EchartVO();
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一个月排班情况饼图");
        echartVO.setSeriesNames(seriesNames);

        return Result.success(echartVO);
    }

    @GetMapping("/scheUserStatus")
    public Result scheUserStatus(@RequestParam String deptId) {
        QueryWrapper userWrapper = new QueryWrapper();
        userWrapper.eq("dept_id",deptId);
        List<UserDepartment> userDepartmentList = userDepartmentService.list(userWrapper);

        List<User> userList = userDepartmentList.stream().map(item -> {
            return userService.getById(item.getUserId());
        }).collect(Collectors.toList());

        List<String> xAxis = new LinkedList<>();
        List<List> yAxis = new LinkedList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        QueryWrapper shiftWrapper = new QueryWrapper();
        shiftWrapper.eq("dept_id", deptId);
        List<Shift> shiftList = shiftService.list(shiftWrapper); // 查询班次
        List<String> seriesNames = shiftList.stream().map(item -> {
            return item.getShiftName();
        }).collect(Collectors.toList());

        xAxis = userList.stream().map(item -> item.getUserName()).collect(Collectors.toList());

        for (Shift shift : shiftList) {
            List<Integer> list = new LinkedList<>();
            for (User user : userList) {
                QueryWrapper scheWrapper = new QueryWrapper();
                scheWrapper.eq("dept_id", deptId);
                scheWrapper.ge("sche_date",oneMonthAgo);
                scheWrapper.le("sche_date", today);
                scheWrapper.eq("user_id", user.getUserId());
                scheWrapper.eq("shift_id",shift.getShiftId());

                List list1 = schedulingService.list(scheWrapper);
                list.add(list1.size());
            }
            yAxis.add(list);
        }


        EchartVO echartVO = new EchartVO();
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一月医护人员上班概览");
        echartVO.setSeriesNames(seriesNames);
        echartVO.setXAxis(xAxis);

        return Result.success(echartVO);
    }

    @GetMapping("/scheCompltStatus")
    public Result scheCompltStatus(@RequestParam String deptId) {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        // 构建最近一个月的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = oneMonthAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateList.add(date);
        }

        List<Double> list = new LinkedList<>();
        for (LocalDate localDate : dateList) {
            QueryWrapper userWrapper = new QueryWrapper();
            userWrapper.eq("dept_id",deptId);
            userWrapper.eq("sche_date", localDate);

            List list1 = schedulingService.list(userWrapper);
            userWrapper.eq("sche_status", 2);
            List list2 = schedulingService.list(userWrapper);
            if(list1.size() == 0) {
                list.add(100.0);
            } else {
                list.add((double)list2.size() * 100/ (double) list1.size());
            }

        }

        List<List> yAxis = new LinkedList<>();
        List<String> seriesNames = new ArrayList<>();
        seriesNames.add("达成率");
        yAxis.add(list);

        EchartVO echartVO = new EchartVO();
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("排班达成率");
        echartVO.setSeriesNames(seriesNames);
        echartVO.setXAxis(dateList);

        return Result.success(echartVO);
    }

    @GetMapping("/scheHoursStatus")
    public Result scheHoursStatus(@RequestParam String deptId) {
        QueryWrapper userWrapper = new QueryWrapper();
        userWrapper.eq("dept_id",deptId);
        List<UserDepartment> userDepartmentList = userDepartmentService.list(userWrapper);

        List<User> userList = userDepartmentList.stream().map(item -> {
            return userService.getById(item.getUserId());
        }).collect(Collectors.toList());

        // 构建最近一个月的日期列表
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = oneMonthAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateList.add(date);
        }

        List<List> yAxis = new LinkedList<>();
        for (User user : userList) {
            List list = new LinkedList();
            Double hours = 0.0;
            for (LocalDate localDate : dateList) {
                QueryWrapper userDayWrapper = new QueryWrapper();
                userDayWrapper.eq("dept_id", deptId);
                userDayWrapper.eq("sche_date", localDate);
                userDayWrapper.eq("user_id", user.getUserId());

                List<Scheduling> list1 = schedulingService.list(userDayWrapper);
                for (Scheduling scheduling : list1) {
                    Shift shift = shiftService.selectOneAll(scheduling.getShiftId());
                    LocalTime shiftBegintime = shift.getShiftBegintime();
                    LocalTime shiftEndtime = shift.getShiftEndtime();
                    hours += calculateHourDifference(shiftBegintime, shiftEndtime);
                }
                list.add(hours);
            }
            yAxis.add(list);
        }
        List<String> seriesNames = userList.stream().map(item -> {
            return item.getUserName();
        }).collect(Collectors.toList());

        EchartVO echartVO = new EchartVO();
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一月人员累计工时概览");
        echartVO.setSeriesNames(seriesNames);
        echartVO.setXAxis(dateList);

        return Result.success(echartVO);
    }

    @GetMapping("/dashboard")
    public Result dashboard(@RequestParam String deptId) {
        List<Double> ans = new LinkedList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        QueryWrapper scheWrapper = new QueryWrapper();
        scheWrapper.eq("dept_id",deptId);
        scheWrapper.ge("sche_date",oneMonthAgo);
        scheWrapper.le("sche_date", today);
        List list = schedulingService.list(scheWrapper);
        ans.add((double) list.size());

        scheWrapper.eq("sche_status", 2);
        List list1 = schedulingService.list(scheWrapper);
        ans.add((double) list1.size() * 100/ (double) list.size());

        QueryWrapper userWrapper = new QueryWrapper();
        scheWrapper.eq("dept_id",deptId);
        List list2 = userDepartmentService.list(userWrapper);
        ans.add((double)list2.size());

        return Result.success(ans);

    }

    @GetMapping("/user/dashboard")
    public Result userDashboard(@RequestParam String userId) {
        List<Double> ans = new LinkedList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        QueryWrapper scheWrapper = new QueryWrapper();
        scheWrapper.eq("user_id",userId);
        scheWrapper.ge("sche_date",oneMonthAgo);
        scheWrapper.le("sche_date", today);
        List list = schedulingService.list(scheWrapper);
        ans.add((double) list.size());

        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.between("sche_date", oneMonthAgo, today);
        List<Scheduling> schedulingList = schedulingService.list(queryWrapper);
        double totalHours = 0.0;
        for (Scheduling scheduling : schedulingList) {
            Shift shift = shiftService.selectOneAll(scheduling.getShiftId());
            if (shift != null) {
                LocalTime beginTime = shift.getShiftBegintime();
                LocalTime endTime = shift.getShiftEndtime();
                totalHours += calculateHourDifference(beginTime, endTime);
            }
        }
        ans.add(totalHours);

        return Result.success(ans);
    }

    @GetMapping("/user/scheHoursStatus")
    public Result scheUserHoursStatus(@RequestParam String userId) {
        // 构建最近一个月的日期列表
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = oneMonthAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateList.add(date);
        }

        List<Double> userHoursList = new LinkedList<>();
        double totalHours = 0.0;

        for (LocalDate localDate : dateList) {
            QueryWrapper<Scheduling> dayWrapper = new QueryWrapper<>();
            dayWrapper.eq("user_id", userId);
            dayWrapper.eq("sche_date", localDate);

            List<Scheduling> dailySchedule = schedulingService.list(dayWrapper);
            for (Scheduling scheduling : dailySchedule) {
                Shift shift = shiftService.selectOneAll(scheduling.getShiftId());
                if (shift != null) {
                    LocalTime begin = shift.getShiftBegintime();
                    LocalTime end = shift.getShiftEndtime();
                    totalHours += calculateHourDifference(begin, end);
                }
            }
            userHoursList.add(totalHours); // 累计值
        }

        User user = userService.getById(userId);

        EchartVO echartVO = new EchartVO();
        echartVO.setXAxis(dateList);                    // 日期列表
        echartVO.setYAxis(Collections.singletonList(userHoursList));  // y轴数据（包一层 List）
        echartVO.setSeriesNames(Collections.singletonList(user.getUserName())); // 用户名
        echartVO.setTitle("近一月个人累计工时变化");

        return Result.success(echartVO);
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

    @GetMapping("/user/scheStatus")
    public Result scheOneUserStatus(@RequestParam String userId) {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        // 获取该用户对象（用于查班次 & 名字）
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        //以下为修改
        QueryWrapper userWrapper = new QueryWrapper();
        userWrapper.eq("user_id", userId);
        userWrapper.ge("sche_date", oneMonthAgo);
        userWrapper.le("sche_date", today);
        List<Scheduling> allScheList = schedulingService.list(userWrapper);
        HashSet<Integer> set = new HashSet<>();
        for (Scheduling scheduling : allScheList) {
            set.add(scheduling.getShiftId());
        }
        List<Integer> shiftIdList = new ArrayList<>(set);
        List<Shift> shiftList = new LinkedList<>();
        for (Integer integer : shiftIdList) {
            shiftList.add(shiftService.selectOneAll(integer));
        } //以上为修改

//        // 获取该用户所在的部门（前提：一个用户对应一个部门）
//        QueryWrapper<UserDepartment> userDeptWrapper = new QueryWrapper<>();
//        userDeptWrapper.eq("user_id", userId);
//        UserDepartment userDepartment = userDepartmentService.getOne(userDeptWrapper);
//        if (userDepartment == null) {
//            return Result.error("用户未关联部门");
//        }
//
//        String deptId = userDepartment.getDeptId();
//
//        // 查询该部门的所有班次
//        QueryWrapper<Shift> shiftWrapper = new QueryWrapper<>();
//        shiftWrapper.eq("dept_id", deptId);
//        List<Shift> shiftList = shiftService.list(shiftWrapper);
//
        List<String> seriesNames = new LinkedList<>();
        seriesNames.add("全部");
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = oneMonthAgo; !date.isAfter(today); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 全部排班数量
        List<Integer> totalList = new LinkedList<>();
        for (LocalDate date : dateList) {
            QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("sche_date", date);
            queryWrapper.eq("user_id", userId);
            List<Scheduling> schedulings = schedulingService.list(queryWrapper);
            totalList.add(schedulings.size());
        }
        List<List> yAxis = new LinkedList<>();
        yAxis.add(totalList);

        // 按班次统计
        for (Shift shift : shiftList) {
            List<Integer> list = new LinkedList<>();
            for (LocalDate date : dateList) {
                QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sche_date", date);
                queryWrapper.eq("user_id", userId);
                queryWrapper.eq("shift_id", shift.getShiftId());
                List<Scheduling> schedulings = schedulingService.list(queryWrapper);
                list.add(schedulings.size());
            }
            seriesNames.add(shift.getShiftName());
            yAxis.add(list);
        }

        EchartVO echartVO = new EchartVO();
        echartVO.setXAxis(dateList);
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一月个人排班统计");
        echartVO.setSeriesNames(seriesNames);

        return Result.success(echartVO);
    }

    @GetMapping("/user/schePieStatus")
    public Result scheOnePieStatus(@RequestParam String userId) {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

//        // 获取该用户所在的部门（用于查对应班次）
//        QueryWrapper<UserDepartment> userDeptWrapper = new QueryWrapper<>();
//        userDeptWrapper.eq("user_id", userId);
//        UserDepartment userDepartment = userDepartmentService.getOne(userDeptWrapper);
//        if (userDepartment == null) {
//            return Result.error("用户未关联部门");
//        }
//
//        String deptId = userDepartment.getDeptId();
//
//        // 查询该部门下的所有班次
//        QueryWrapper<Shift> shiftWrapper = new QueryWrapper<>();
//        shiftWrapper.eq("dept_id", deptId);
//        List<Shift> shiftList = shiftService.list(shiftWrapper);
        //以下为修改
        QueryWrapper userWrapper = new QueryWrapper();
        userWrapper.eq("user_id", userId);
        userWrapper.ge("sche_date", oneMonthAgo);
        userWrapper.le("sche_date", today);
        List<Scheduling> allScheList = schedulingService.list(userWrapper);

        HashSet<Integer> set = new HashSet<>();
        for (Scheduling scheduling : allScheList) {
            set.add(scheduling.getShiftId());
        }
        List<Integer> shiftIdList = new ArrayList<>(set);
        List<Shift> shiftList = new LinkedList<>();
        for (Integer integer : shiftIdList) {
            shiftList.add(shiftService.selectOneAll(integer));
        } //以上为修改

        // 近一月时间范围

        List<String> seriesNames = new LinkedList<>();
        List<Integer> countList = new LinkedList<>();

        for (Shift shift : shiftList) {
            seriesNames.add(shift.getShiftName());

            QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("shift_id", shift.getShiftId());
            queryWrapper.ge("sche_date", oneMonthAgo);
            queryWrapper.le("sche_date", today);

            List<Scheduling> userShiftSchedules = schedulingService.list(queryWrapper);
            countList.add(userShiftSchedules.size());
        }

        List<List> yAxis = new LinkedList<>();
        yAxis.add(countList);

        EchartVO echartVO = new EchartVO();
        echartVO.setYAxis(yAxis);
        echartVO.setTitle("近一月个人排班分布（饼图）");
        echartVO.setSeriesNames(seriesNames);

        return Result.success(echartVO);
    }

    public  double calculateHourDifference(LocalTime shiftBegintime, LocalTime shiftEndtime) {
        Duration duration = Duration.between(shiftBegintime, shiftEndtime);
        return duration.toMinutes() / 60.0;
    }

}
