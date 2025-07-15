package com.tangdeng.hssystem.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tangdeng.hssystem.pojo.dto.ChangePwdDTO;
import com.tangdeng.hssystem.pojo.dto.UserDTO;
import com.tangdeng.hssystem.pojo.dto.UserEmailLoginDTO;
import com.tangdeng.hssystem.pojo.entity.Scheduling;
import com.tangdeng.hssystem.pojo.entity.User;
import com.tangdeng.hssystem.pojo.entity.UserDepartment;
import com.tangdeng.hssystem.pojo.pagebean.PageBean;
import com.tangdeng.hssystem.pojo.vo.UserLoginVO;
import com.tangdeng.hssystem.pojo.vo.UserVO;
import com.tangdeng.hssystem.result.Result;
import com.tangdeng.hssystem.service.DepartmentService;
import com.tangdeng.hssystem.service.SchedulingService;
import com.tangdeng.hssystem.service.UserDepartmentService;
import com.tangdeng.hssystem.service.UserService;
import com.tangdeng.hssystem.utils.JWTUtils;
import com.tangdeng.hssystem.utils.MD5Utils;
import com.tangdeng.hssystem.utils.MailUtils;
import com.tangdeng.hssystem.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController //包含了@ResponseBody
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    MailUtils mailUtils;
    @Autowired
    UserDepartmentService userDepartmentService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    SchedulingService schedulingService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String userId = userDTO.getUserId();
        String userPwd = userDTO.getUserPwd();
        if(userId == null) {
            return Result.error();
        }
        User byId = userService.getById(userId);
        if(byId != null) {
            if(MD5Utils.checkMD5(userPwd, byId.getUserPwd())) {
                //生成token
                HashMap<String, String> tokenMap = new HashMap<>();
                tokenMap.put("userId", byId.getUserId());
                String token = JWTUtils.getToken(tokenMap);
                //生成存入redis的map
                HashMap<String, String> userInfo = new HashMap<>();
                //token存入redis的Map中
                userInfo.put("token", token);
                redisUtils.set(byId.getUserId(), userInfo);
                return Result.success(new UserLoginVO(token));
            } else {
                return Result.error("账号密码错误");
            }
        }
        return Result.error("账号密码错误");
    }

    @GetMapping("/verifycode/{userid}")
    public Result getVerifyCode(@PathVariable("userid") String userid) {
        //处理用户不存在的情况
        User byId = userService.getById(userid);
        if(byId == null) return Result.error("用户不存在");
        //处理用户存在的情况不会直接删除redis已经存在的token,这样操作不会挤掉线上的人
        HashMap<String, String> userInfo = (HashMap<String, String>) redisUtils.get(userid);
        String code = MailUtils.generateCaptcha();
         if (userInfo == null) {
             userInfo = new HashMap<>();
             userInfo.put("verifycode", code);
         } else {
             userInfo.put("verifycode", code);
         }
         redisUtils.set(userid,userInfo);
         mailUtils.sendTextMailMessage(byId.getUserEmail(),"登录验证码", code);
         return Result.success();
    }

    @PostMapping("/emaillogin")
    public Result emaillogin(@RequestBody UserEmailLoginDTO userEmailLoginDTO) {
        User byId = userService.getById(userEmailLoginDTO.getUserId());
        if(byId == null) return Result.error("用户不存在");
        else {
            String userId = userEmailLoginDTO.getUserId();
            HashMap<String, String> hashMap = (HashMap<String, String>) redisUtils.get(userId);
            String tokenVerifyCode = hashMap.get("verifycode");
            if(tokenVerifyCode == null) return Result.error("验证码错误");
            if(tokenVerifyCode.equals(userEmailLoginDTO.getVerifyCode())) {
                //生成token
                HashMap<String, String> tokenMap = new HashMap<>();
                tokenMap.put("userId", userId);
                String token = JWTUtils.getToken(tokenMap);
                //生成存储于redis中的Map
                HashMap<String, String> userInfo = new HashMap<>();
                //token存入redis的Map中
                userInfo.put("token", token);
                redisUtils.set(userId, userInfo);
                return Result.success(new UserLoginVO(token));
            }else {
                return Result.error("验证码错误");
            }
        }
    }

    @PostMapping
    public Result addOne(@RequestBody UserDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);
        if(user.getUserId() == null) {
            return Result.error();
        }
        User byId = userService.selectOneAll(user.getUserId());
        if(byId != null) {
            return Result.error("用户已存在");
        }
//        List byEmail = userService.getByEmail(userDTO.getUserEmail());
//        if(!byEmail.isEmpty()) {
//            return Result.error("用户邮箱已注册");
//        }
        user.setUserAvatar("https://hs-system.oss-cn-hangzhou.aliyuncs.com/5e2777d449887dbee4e498bcde37036.png");
        user.setUserRegtime(new Date());
        String userPwd = user.getUserPwd();
        userPwd = MD5Utils.createMD5(userPwd);
        user.setUserPwd(userPwd);
        userService.save(user);
        return Result.success();
    }

    @DeleteMapping("/{userid}")
    public Result deleteOne(@PathVariable("userid") String userid){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userid);
        userDepartmentService.remove(queryWrapper);

        Date now = new Date();
        QueryWrapper userWrapper = new QueryWrapper();
        userWrapper.eq("user_id", userid);
        userWrapper.ge("sche_date", now);
        List<Scheduling> list = schedulingService.list(queryWrapper);
        for (Scheduling scheduling : list) {
            scheduling.setScheStatus(1);
            schedulingService.updateById(scheduling);
            UpdateWrapper<Scheduling> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("user_id", null).eq("sche_id", scheduling.getScheId());
            schedulingService.update(updateWrapper);
        }
        boolean b = userService.removeById(userid);
        if(b) return Result.success();
        else return Result.error();
    }

    @DeleteMapping("/batch")
    public Result deleteBatch(@RequestBody List<String> userIds) {
        for (String userId : userIds) {
            deleteOne(userId);
        }
        return Result.success();
    }

    @GetMapping("/{userid}")
    public Result getOne(@PathVariable("userid") String userid) {
        User byId = userService.getById(userid);
        if(byId == null) {
            return Result.error("ID为空，查找失败");
        }
        UserVO userVO = BeanUtil.copyProperties(byId, UserVO.class);
        return Result.success(userVO);
    }

    @PutMapping("/{userid}")
    public Result updateOne(@PathVariable("userid") String userid ,@RequestBody UserDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);
        boolean b = userService.updateById(user);
        if(b) {
            User byId = userService.getById(userid);
            UserVO userVO = BeanUtil.copyProperties(byId, UserVO.class);
            return Result.success(userVO);
        }else {
            return Result.error();
        }
    }

    @GetMapping()
    public Result getAll(@RequestParam(required = false) String deptId) {
        if(deptId != null) {
            QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dept_id", deptId);
            List<UserDepartment> list = userDepartmentService.list(queryWrapper);
            List<User> users = new LinkedList<>();
            for (UserDepartment userDepartment : list) {
                User byId = userService.getById(userDepartment.getUserId());
                users.add(byId);
            }
            List<UserVO> userVOS = users.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class)).collect(Collectors.toList());
            return Result.success(userVOS);
        } else {
            return Result.success(userService.list(),"用户列表获取成功");
        }
    }

    @GetMapping("/doctor")
    public Result getDoctorAll(@RequestParam(required = false) String deptId) {
        if(deptId != null) {
            QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dept_id", deptId);
            List<UserDepartment> list = userDepartmentService.list(queryWrapper);
            List<User> users = new LinkedList<>();
            for (UserDepartment userDepartment : list) {
                User byId = userService.getById(userDepartment.getUserId());
                if(byId.getUserType() == 1)
                    users.add(byId);
            }
            List<UserVO> userVOS = users.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class)).collect(Collectors.toList());
            return Result.success(userVOS);
        } else {
            return Result.success(userService.list(),"用户列表获取成功");
        }
    }


    @GetMapping("/nurse")
    public Result getNurseAll(@RequestParam(required = false) String deptId) {
        if(deptId != null) {
            QueryWrapper<UserDepartment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("dept_id", deptId);
            List<UserDepartment> list = userDepartmentService.list(queryWrapper);
            List<User> users = new LinkedList<>();
            for (UserDepartment userDepartment : list) {
                User byId = userService.getById(userDepartment.getUserId());
                if(byId.getUserType() == 2)
                    users.add(byId);
            }
            List<UserVO> userVOS = users.stream().map(item -> BeanUtil.copyProperties(item, UserVO.class)).collect(Collectors.toList());
            return Result.success(userVOS);
        } else {
            return Result.success(userService.list(),"用户列表获取成功");
        }
    }

    @GetMapping("/admin/dashboard")
    public Result getDashBord() {
        List<Integer> ans = new LinkedList<>();
        ans.add(departmentService.list().size());
        ans.add(userService.list().size());
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("user_type", 1);
        ans.add(userService.list(queryWrapper1).size());
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq("user_type", 2);
        ans.add(userService.list(queryWrapper2).size());
        return Result.success(ans);
    }

    @PutMapping("/changepwd/{userid}")
    public Result changePwd(@PathVariable String userid, @RequestBody ChangePwdDTO changePwdDTO) {
        String userPwd = MD5Utils.createMD5(changePwdDTO.getUserPwd());
        User user = userService.getById(userid);
        user.setUserPwd(userPwd);
        userService.updateById(user);
        return Result.success();
    }



    @GetMapping ("/pages")
    public Result getAllPages(@RequestParam(required = true) Integer pageNum,
                              @RequestParam(required = true) Integer pageSize,
                              @RequestParam(required = false) String userId,
                              @RequestParam(required = false) String userName,
                              @RequestParam(required = false) Integer userType,
                              @RequestParam(required = false) Integer deptId) {

        QueryWrapper queryWrapper = new QueryWrapper();
        if(userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if(userName != null) {
            queryWrapper.like("user_name", userName);
        }
        if(userType != null) {
            queryWrapper.eq("user_type", userType);
        }
        PageBean pages = userService.getPages(pageNum, pageSize, queryWrapper);
        if(deptId == null) {
            return Result.success(pages);
        } else {
            List<User> items = pages.getItems();
            QueryWrapper userWrapper = new QueryWrapper();
            userWrapper.eq("dept_id", deptId);
            List<UserDepartment> list = userDepartmentService.list(userWrapper);

            List<User> filteredItems = items.stream()
                    .filter(user -> list.stream()
                            .anyMatch(ud -> ud.getUserId().equals(user.getUserId())))
                    .collect(Collectors.toList());
            pages.setItems(filteredItems);

            return Result.success(pages);
        }
    }
}
