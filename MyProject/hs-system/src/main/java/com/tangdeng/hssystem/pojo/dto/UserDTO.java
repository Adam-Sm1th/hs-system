package com.tangdeng.hssystem.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    String userId;
    String userPwd;
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
