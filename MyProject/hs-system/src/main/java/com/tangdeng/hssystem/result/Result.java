package com.tangdeng.hssystem.result;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code; //编码
    private String msg; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = Status.OK.getCode();
        result.msg = Status.OK.getMessage();
        return result;
    }

    public static <T> Result<T> success(String msg) {
        Result result = new Result();
        result.code = Status.OK.getCode();
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = Status.OK.getCode();
        result.msg = Status.OK.getMessage();
        return result;
    }

    public static <T> Result<T> success(T object, String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = Status.OK.getCode();
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error() {
        Result result = new Result();
        result.code = Status.SYSTEM_ERROR.getCode();
        result.msg = Status.SYSTEM_ERROR.getMessage();
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.code = Status.SYSTEM_ERROR.getCode();
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(Status status) {
        Result result = new Result();
        result.code = status.getCode();
        result.msg = status.getMessage();
        return result;
    }

}