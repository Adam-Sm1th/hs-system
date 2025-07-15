package com.tangdeng.hssystem.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class SchedulingVO {
    Integer scheId;
    String scheName;
    String scheInfo;
    Date scheDate;
    Integer userType;
    String userId;
    Integer shiftId;
    Integer scheStatus;
    Integer deptId;
    ShiftVO shiftVO;
    UserVO userVO;
}
