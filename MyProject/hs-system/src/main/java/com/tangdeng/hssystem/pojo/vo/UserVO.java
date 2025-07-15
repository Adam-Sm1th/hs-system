package com.tangdeng.hssystem.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    String userId;
    String userName;
    String userGender;
    String userPhone;
    String userEmail;
    String userInfo;
    String userAvatar;
    Integer userPermission;
    Date userBirthday;
    Date userRegtime;
    Integer userType;
    Integer userProactivity;
}
