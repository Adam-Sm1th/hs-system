package com.tangdeng.hssystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
@TableName("hs_shift")
public class Shift {
    @TableId(type = IdType.AUTO)
    Integer shiftId;
    String shiftName;
    LocalTime shiftBegintime;
    LocalTime  shiftEndtime;
    String deptId;
    Integer shiftStyleId;
    @TableLogic
    Integer isDeleted;
}
