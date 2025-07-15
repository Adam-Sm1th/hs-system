package com.tangdeng.hssystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("hs_dept")
public class Department {
    @TableId(type = IdType.INPUT)
    String deptId;
    String deptName;
    String deptInfo;
    Date deptCreatetime;

    @TableLogic
    Integer isDeleted;
}
