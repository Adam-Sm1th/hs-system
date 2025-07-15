package com.tangdeng.hssystem.pojo.dto;
import lombok.Data;

import java.util.Date;

@Data
public class CshiftAddDTO {
    Integer scheId;
    Date cshiftTargetDay;
    Integer shiftTargetId;
    String cshiftInfo;
}
