package com.tangdeng.hssystem.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class LeaveBatchDTO {
    private List<Integer> leaveIds;
    private Boolean auditOperator;
}
