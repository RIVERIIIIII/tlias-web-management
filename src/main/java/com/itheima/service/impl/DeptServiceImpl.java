package com.itheima.service.impl;

import com.itheima.mapper.DeptMapper;
import com.itheima.pojo.Dept;
import com.itheima.service.DeptService;
import com.itheima.config.CacheNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;

    /*
    * Mapper 没有实现类，是因为 MyBatis 利用了动态代理机制，
    * 把“执行 SQL 并返回结果”这一类高度固定的逻辑抽象并自动实现了。
    * （属于mybatis优化）
    * */

    //查询部门列表
    @Cacheable(cacheNames = CacheNames.DEPT_LIST, key = "'all'", sync = true)
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }
    //删除部门信息
    @CacheEvict(cacheNames = CacheNames.DEPT_LIST, allEntries = true)
    @Override
    public void deleteById(Integer id) {
        deptMapper.deleteById(id);
    }
    //添加部门信息
    @CacheEvict(cacheNames = CacheNames.DEPT_LIST, allEntries = true)
    @Override
    public void add(Dept dept) {
        //补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());

        deptMapper.insert(dept);
    }
    //根据id查询部门信息
    @Override
    public Dept getById(Integer id) {
        Dept dept = deptMapper.getById(id);
        return dept;
    }
    //修改部门信息
    @CacheEvict(cacheNames = CacheNames.DEPT_LIST, allEntries = true)
    @Override
    public void update(Dept dept) {
        //补全基础属性
        dept.setUpdateTime(LocalDateTime.now());

        deptMapper.update(dept);
    }
}
