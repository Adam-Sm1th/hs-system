package com.tangdeng.hssystem.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangdeng.hssystem.pojo.entity.Shift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShiftMapper extends BaseMapper<Shift> {
    @Select("SELECT * FROM hs_shift ${ew.customSqlSegment}")
    Shift selectOneAll(@Param("ew") Wrapper<Shift> wrapper);

}
