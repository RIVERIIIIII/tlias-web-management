package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.StudentMapper;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;
import com.itheima.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public PageResult<Student> page(StudentQueryParam param) {
        //设置分页查询
        PageHelper.startPage(param.getPage(), param.getPageSize());
        //查询基础信息
        List<Student> list = studentMapper.list(param);
        //封装
        Page<Student> p = (Page<Student>) list;
        return new PageResult<Student>(p.getTotal(), p.getResult());
    }

    @Override
    public void save(@RequestBody Student student) {
        //补全基础属性（违纪次数和违纪扣分已经在mysql设置了默认值）
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        //保存
        System.out.println( student);
        studentMapper.insert(student);
    }

    @Override
    public Student getById(Integer id) {
        Student student = studentMapper.getById(id);
        return student;
    }

    @Override
    public void updateById(Student student) {
        //更新基础属性
        student.setUpdateTime(LocalDateTime.now());

        studentMapper.updateById(student);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        //批量删除学生
        studentMapper.deleteByIds(ids);
    }

    @Override
    public void violation(Integer id, Integer score) {
        studentMapper.violation(id, score);
    }
}
