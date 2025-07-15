package com.tangdeng.hssystem.pojo.dto;


import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Data
public class ShiftDTO {
    Integer shiftId;
    String shiftName;
    LocalTime shiftBegintime;
    LocalTime  shiftEndtime;
    String deptId;
    Integer shiftStyleId;
}
