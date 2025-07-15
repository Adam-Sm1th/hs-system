package com.tangdeng.hssystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hs_user_dept")
public class UserDepartment {
    String userId;
    String deptId;
}
