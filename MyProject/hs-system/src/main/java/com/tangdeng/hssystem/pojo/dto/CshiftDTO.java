package com.tangdeng.hssystem.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CshiftDTO {
    Integer cshiftId;
    String userId;
    Integer scheOriginalId;
    String cshiftInfo;
    Integer scheTargetId;
    Date cshiftTargetDay;
    Integer shiftTargetId;
    Integer cshiftStatus;
    Date cshiftOriginalDay;
    String deptId;
}
