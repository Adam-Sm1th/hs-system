package com.tangdeng.hssystem.pojo.dto;
import lombok.Data;

import java.util.Date;

@Data
public class SchedulingDTO {
    Integer scheId;
    String scheName;
    String scheInfo;
    Date scheDate;
    Integer userType;
    String userId;
    Integer shiftId;
    Integer scheStatus;
    Integer deptId;
    Integer userNum;
}
