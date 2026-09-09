package com.itheima.controller;

import com.itheima.anno.LogOperation;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.ClazzQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//班级管理控制器
@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    //班级列表查询
    @GetMapping
    public Result list(ClazzQueryParam  param){
        log.info("查询班级列表：{}",  param);
        PageResult<Clazz> pageResult = clazzService.list(param);
        return Result.success(pageResult);
    }

    //添加班级
    @PostMapping
    @LogOperation
    public Result save(@RequestBody Clazz clazz){//接收json数据，将json数据封装为对象
        log.info("添加班级：{}", clazz);
        clazzService.save(clazz);
        return Result.success();
    }

    //修改班级——查询回显/根据id查询
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){//当参数名称和形参名称一致时，可以省略@PathVariable注解【后的属性值("id")】
        log.info("查询班级信息：{}", id);
        Clazz clazz = clazzService.getInfo(id);
        return Result.success(clazz);
    }

    //修改班级——更新班级信息
    @PutMapping
    @LogOperation
    public Result update(@RequestBody Clazz clazz){//接收json数据，将json数据封装为对象
        log.info("更新班级信息：{}", clazz);
        clazzService.update(clazz);
        return Result.success();
    }

    //删除班级
    @DeleteMapping("/{id}")
    @LogOperation
    public Result delete(@PathVariable Integer id){
        log.info("删除班级：{}", id);
        clazzService.deleteById(id);
        return Result.success();
    }

    //查询所有班级名称
    @GetMapping("/list")
    public Result getList(){
        log.info("查询所有班级");
        List<Clazz> list = clazzService.getList();
        return Result.success(list);
    }
}
