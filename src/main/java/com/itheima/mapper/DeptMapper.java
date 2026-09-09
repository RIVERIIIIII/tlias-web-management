package com.itheima.mapper;

import com.itheima.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
* 部门信息
* */
@Mapper
public interface DeptMapper {
    /*
     * mybatis 默认数据封装规则：
     *  数据库字段名和实体类属性名一致
     * 不一致的解决方式：
     * 1.使用@Results注解，指定数据库字段和实体类属性的映射关系
     * 2.起别名，使数据库字段和实体类属性名一致
     * 3.在application.yml文件开启驼峰映射，
     *   数据库字段使用下划线命名，实体类属性使用驼峰命名
     * */
    /*解决方式一：
    @Results({
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "createTime")
    })*/
    /*解决方式二：
    @Select("select id, name, create_time createTime, update_time createTime from dept order by update_time desc ")*/
    //查询部门列表
    @Select("select id, name, create_time, update_time from dept order by update_time desc ")
    List<Dept> findAll();

    //删除部门信息
    @Delete("delete from dept where id = #{id}")
    void deleteById(Integer id);

    //添加部门信息
    @Insert(("insert into dept (name, create_time, update_time) values (#{name}, #{createTime}, #{updateTime})"))
    /*如果在mapper接口中，需要传递多个参数，可以把多个参数封装到一个对象中。
    在SQL语句中获取参数的时候，#{...} 里面写的是对象的属性名【注意是属性名，不是表的字段名】*/
    void insert(Dept dept);

    //根据ID查询部门信息
    @Select("select id, name, create_time, update_time from dept where id = #{id}")
    Dept getById(Integer id);

    //修改部门信息
    @Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
    void update(Dept dept);
}