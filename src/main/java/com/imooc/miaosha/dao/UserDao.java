package com.imooc.miaosha.dao;


import com.imooc.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


//dao是个接口，而domain是实现
@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") int id);//参数变量通过@Param来定义，用#引用,将int id 命名为id了，传给sql

    @Insert("insert into user(id,name) values(#{id}, #{name})")
    public int insert(User user);
}
