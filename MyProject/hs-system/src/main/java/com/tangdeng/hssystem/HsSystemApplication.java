package com.tangdeng.hssystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HsSystemApplication.class, args);
    }

}
/*
maven中的mybatis要是springboot3版本的否则报错
lombok版本配置要统一否则406
mail要指定ssl连接
重复创建主键冲突
处理冲突
 */