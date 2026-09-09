package com.itheima.controller;

import com.itheima.anno.LogOperation;
import com.itheima.pojo.Dept;
import com.itheima.pojo.Result;
import com.itheima.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//部门管理控制器
@Slf4j//日志注解，自动生成日志对象
@RequestMapping("/depts")//抽取方法中的公共路径
@RestController//json格式响应给前端（RESTful风格）
public class DeptController {

    @Autowired
    private DeptService deptService;

    //查询部门列表
    @GetMapping//选定请求方式为GET（RESTful风格）
    /*@RequestMapping(value = "/depts",method = RequestMethod.GET)*/
    public Result findAll(){
        List<Dept> deptList = deptService.findAll();
        log.info("查询全部部门信息");
        return Result.success(deptList);
    }

    //删除部门信息
    @DeleteMapping
    @LogOperation
    /*数据接收方式：@RequestParam
    查询参数，使用http://localhost:8080/depts?id=1查询 -> 一般用于过滤
    其中当参数名称和形参名称一致时，可以省略@RequestParam注解
    注意一旦声明@RequestParam注解，就必须在请求时传递该参数（required默认为true）*/
    public Result deleteById(/*@RequestParam("id")*/ Integer id){
        log.info("根据ID删除部门：{}", id);
        deptService.deleteById(id);
        return Result.success();
    }

    //新增部门
    @PostMapping
    @LogOperation
    /*数据接收方式：@RequestBody -> 一般用于获取json格式数据
      接收json格式的数据，将json数据封装为对象
      注意JSON数据的键名与方法形参对象的属性名相同*/
    public Result add(@RequestBody Dept dept)
        {
            log.info("新增部门：{}", dept);
            deptService.add(dept);
            return Result.success();
        }

    //根据id查询部门信息
    @GetMapping("/{id}")
    /*数据接收方式：@PathVariable -> 一般用于获取单个资源
    路径参数，使用http://localhost:8080/depts/1查询
    其中当参数名称和形参名称一致时，可以省略@PathVariable注解【后的属性值("id")】*/
    public Result getById(@PathVariable/*("id")*/ Integer id){
        log.info("根据ID查询部门：{}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    //修改部门信息
    @PutMapping
    @LogOperation
    /*数据接收方式：@RequestBody
    接收json格式的数据，将json数据封装为对象
    注意JSON数据的键名与方法形参对象的属性名相同*/
    public Result update(@RequestBody Dept dept){
        log.info("修改部门：{}", dept);
        deptService.update(dept);
        return Result.success();
    }
}