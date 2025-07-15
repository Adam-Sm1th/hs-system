package com.tangdeng.hssystem.pojo.vo;


import lombok.Data;

import java.util.Date;

@Data
public class DepartmentVO {
    String deptId;
    String deptName;
    String deptInfo;
    Date deptCreatetime;
}
