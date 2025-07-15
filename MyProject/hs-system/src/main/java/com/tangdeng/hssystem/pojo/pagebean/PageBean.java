package com.tangdeng.hssystem.pojo.pagebean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageBean<T> {
    private Long pages; //一共多少页
    private Long current; //当前页码值
    private Long size; // 每页显示数
    private long total; //共多少条数据
    private List<T> items;
}
