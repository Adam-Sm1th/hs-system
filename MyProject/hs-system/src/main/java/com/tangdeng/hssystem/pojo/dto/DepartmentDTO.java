package com.tangdeng.hssystem.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DepartmentDTO {
    String deptId;
    String deptName;
    String deptInfo;
    Date deptCreatetime;
}
