package com.tangdeng.hssystem.pojo.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.apache.ibatis.annotations.One;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@TableName("hs_sche")
public class Scheduling {
    @TableId(type = IdType.AUTO)
    Integer scheId;
    String scheName;
    String scheInfo;
    Date scheDate;
    Integer userType;
    String userId;
    Integer shiftId;
    Integer scheStatus;
    String deptId;

    @TableLogic
    Integer isDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scheduling that = (Scheduling) o;
        return Objects.equals(scheName, that.scheName) &&
                Objects.equals(scheInfo, that.scheInfo) &&
                Objects.equals(scheDate, that.scheDate) &&
                Objects.equals(userType, that.userType) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(shiftId, that.shiftId) &&
                Objects.equals(scheStatus, that.scheStatus) &&
                Objects.equals(deptId, that.deptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheName, scheInfo, scheDate, userType, userId, shiftId, scheStatus, deptId);
    }

}
