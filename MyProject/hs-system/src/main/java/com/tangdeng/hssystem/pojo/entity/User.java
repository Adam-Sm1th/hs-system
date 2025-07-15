package com.tangdeng.hssystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


//@NoArgsConstructor
//@AllArgsConstructor
@Data
@TableName("hs_user")
public class User{
    @TableId(type = IdType.INPUT)
    String userId;
    String userPwd;
    String userName;
    String userGender;
    String userPhone;
    String userEmail;
    String userInfo;
    String userAvatar;
    Integer userPermission;
    Integer userType;
    Integer userProactivity;
    Date userBirthday;
    Date userRegtime;

    @TableLogic
    Integer isDeleted;
}//驼峰自动转化_的格式
