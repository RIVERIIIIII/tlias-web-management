package com.itheima.controller;

import com.github.pagehelper.Page;
import com.itheima.pojo.Emp;
import com.itheima.pojo.EmpQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


//员工管理控制器
@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {
    @Autowired
    private EmpService empService;

    //查询所有员工及其所在部门
    @GetMapping
    /*当参数较少的时候，可以使用形参逐个接收
     *public Result list(@RequestParam(defaultValue = "1") Integer page,//设置默认值
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String name, Integer gender,
                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)*/
    public Result list(EmpQueryParam param){
        log.info("分页查询：{}", param);
        PageResult<Emp> pageResult = empService.page(param);
        return Result.success(pageResult);
    }

    //添加员工
    @PostMapping
    public Result save(@RequestBody Emp emp){//接收json数据，将json数据封装为对象
        log.info("添加员工：{}", emp);
        empService.save(emp);
        return Result.success();
    }

    //删除员工
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){//list接收参数@RequestParam不可省略
        log.info("删除员工：{}", ids);

        empService.deleteByIds(ids);

        return Result.success();
    }

    //修改员工——查询回显/根据id查询
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){//当参数名称和形参名称一致时，可以省略@PathVariable注解【后的属性值("id")】
        log.info("查询员工信息：{}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    //修改员工——更新员工信息和工作经历
    @PutMapping
    public Result update(@RequestBody Emp emp){//接收json数据，将json数据封装为对象
        log.info("更新员工信息：{}", emp);
        empService.update(emp);
        return Result.success();
    }

    //查询全部员工名称
    @GetMapping("/list")
    public Result getList(){
        log.info("查询全部员工");
        List<Emp> list = empService.getList();
        return Result.success(list);
    }
}
