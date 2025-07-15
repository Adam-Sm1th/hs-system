package com.tangdeng.hssystem.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangdeng.hssystem.pojo.entity.Department;
import com.tangdeng.hssystem.pojo.entity.Shift;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    @Select("SELECT * FROM hs_dept ${ew.customSqlSegment}")
    Department selectOneAll(@Param("ew") Wrapper<Department> wrapper);

}
