package com.tangdeng.hssystem.pojo.vo;

import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class ShiftVO {
    Integer shiftId;
    String shiftName;
    LocalTime shiftBegintime;
    LocalTime  shiftEndtime;
    String deptId;
    Integer shiftStyleId;
    Integer isDeleted;
}
