package com.itheima.service;

import com.itheima.pojo.Dept;
import com.itheima.pojo.Result;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DeptService {
    //查询部门列表
    List<Dept> findAll();
    //删除部门信息
    void deleteById(Integer id);
    //添加部门信息
    void add(Dept dept);
    //根据id查询部门信息
    Dept getById(Integer id);
    //修改部门信息
    void update(Dept dept);
}