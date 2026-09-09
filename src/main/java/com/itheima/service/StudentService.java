package com.itheima.service;

import com.itheima.pojo.PageResult;
import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;

import java.util.List;

public interface StudentService {
    PageResult<Student> page(StudentQueryParam param);

    void save(Student student);

    Student getById(Integer id);

    void updateById(Student student);

    void deleteByIds(List<Integer> ids);

    void violation(Integer id, Integer score);
}
