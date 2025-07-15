package com.tangdeng.hssystem.pojo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hs_cshift")
public class Cshift {
    @TableId(type = IdType.AUTO)
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
    @TableLogic
    Integer isDeleted;
}
