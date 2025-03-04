package com.Tc_traveler.IDSOD.mapper;

import com.Tc_traveler.IDSOD.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username=#{username}")
    User findUserByUsername(String username);

    @Insert("insert into user(username,password,create_time,update_time)" + "values(#{username},#{pwd},now(),now())")
    void register(String username, String pwd);

    @Insert("insert into consequence(patient_id,consequence,create_time,update_time)" + "values(#{id},#{s},now(),now())")
    void addConsequence(Integer id, String s);
}
