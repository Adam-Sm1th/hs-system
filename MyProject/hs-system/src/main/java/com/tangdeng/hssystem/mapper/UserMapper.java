package com.tangdeng.hssystem.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangdeng.hssystem.pojo.entity.Shift;
import com.tangdeng.hssystem.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM hs_user ${ew.customSqlSegment}")
    User selectOneAll(@Param("ew") Wrapper<Shift> wrapper);
}
