package com.tangdeng.hssystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hs_leave")
public class Leave {
    @TableId(type = IdType.AUTO)
    Integer leaveId;
    String userId;
    Integer scheId;
    String leaveInfo;
    Integer leaveStatus;
    String deptId;
    Date LeaveDate;
    @TableLogic
    Integer isDeleted;
}
