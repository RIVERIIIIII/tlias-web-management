package com.itheima.controller;

import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.pojo.Student;
import com.itheima.pojo.StudentQueryParam;
import com.itheima.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//学生管理控制器
@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    //查询所有学生信息
    @GetMapping
    public Result list(StudentQueryParam  param){
        log.info("查询所有学生信息");
        PageResult<Student> page = studentService.page(param);
        return Result.success(page);
    }

    //添加学生
    @PostMapping
    public Result save(@RequestBody Student student){
        log.info("添加学生：{}", student);
        studentService.save(student);
        return Result.success();
    }

    //修改学生——查询回显/根据id查询
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("查询学生信息：{}", id);
        Student student = studentService.getById(id);
        return Result.success(student);
    }

    //修改学生——更新学生信息
    @PutMapping
    public Result update(@RequestBody Student student){
        log.info("更新学生信息：{}", student);
        studentService.updateById(student);
        return Result.success();
    }

    //删除学生
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        log.info("删除学生：{}", ids);
        studentService.deleteByIds(ids);
        return Result.success();
    }

    //违纪处理
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score){
        log.info("学生ID：{}，违纪扣分：{}", id, score);
        studentService.violation(id, score);
        return Result.success();
    }

}
