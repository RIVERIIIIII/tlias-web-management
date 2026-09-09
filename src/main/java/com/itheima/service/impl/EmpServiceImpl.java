package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.EmpExprMapper;
import com.itheima.mapper.EmpMapper;
import com.itheima.pojo.*;
import com.itheima.service.EmpLogService;
import com.itheima.service.EmpService;
import com.itheima.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;
    @Autowired
    private EmpLogService empLogService;//引入逻辑层，因为事务管理是在逻辑层进行
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public PageResult<Emp> page(EmpQueryParam param) {
        //1.设置分页参数
        PageHelper.startPage(param.getPage(), param.getPageSize());//作用：告诉MyBatis框架，下一条SQL语句是分页查询，并设置分页参数
        //2.执行查询
        List<Emp> empList = empMapper.list(param);
        //3.解析查询结果并封装
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<Emp>(p.getTotal(), p.getResult());//page.getTotal()：总记录数;page.getResult()：当前页数据
    }

    @Transactional(rollbackFor = {Exception.class})
    //在多次进行数据库操作的方法中，加上事务注解
    // rollbackFor = {Exception.class} ：指定发生所有异常时都回滚事务，默认只有发生运行时异常时回滚事务
    @Override
    public void save(Emp emp) {
        try {
            //补全基础属性
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            //保存员工基本信息
            empMapper.insert(emp);

            //保存员工工作经历
            // 先从接收的数据里面获取出工作经历列表
            List<EmpExpr> exprList = emp.getExprList();
            // 需要从数据库中获取(在mapper使用options注解)到员工ID写入emp中，
            // 然后再拿出来复制到工作经历列表中
            Integer empId = emp.getId();
            if (!CollectionUtils.isEmpty(exprList)) {
                //先遍历写入员工id
                exprList.forEach(empExpr -> empExpr.setEmpId(empId));
                //批量保存
                empExprMapper.insertBatch(exprList);
            }
        }finally{
                //记录操作日志
                EmpLog empLog = new EmpLog(null/*数据库自增*/, LocalDateTime.now(), "添加员工：" + emp.toString());
                empLogService.insertLog(empLog);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void deleteByIds(List<Integer> ids) {
        //批量删除员工
        empMapper.deleteByIds(ids);
        //批量删除员工工作经历
        empExprMapper.deleteByEmpIds(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        Emp emp = empMapper.getById(id);
        return emp;
    }

    @Transactional(rollbackFor = {Exception.class})//在多次进行数据库操作的方法中，加上事务注解
    @Override
    public void update(Emp emp) {
        //1.根据ID更新员工基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);
        //2.删除员工工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));
        //3.添加新的员工工作经历
        // 获取员工id
        Integer empId = emp.getId();
        // 获取员工工作经历列表
        List<EmpExpr> exprList = emp.getExprList();
        // 判断需要添加的工作经历是否为空
        if (!CollectionUtils.isEmpty(exprList)){
            // 遍历列表添加员工id
            exprList.forEach(empExpr -> empExpr.setEmpId(empId));
            // 批量保存员工工作经历
            empExprMapper.insertBatch(emp.getExprList());
        }
    }

    @Override
    public List<Emp> getList() {
        List<Emp> list = empMapper.getList();
        return list;
    }

    @Override
    public LoginInfo login(Emp emp) {
        Emp e = empMapper.selectByUsernameAndPassword(emp);
        if (e != null){
            log.info("用户成功登录：{}", e);
            //生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getId());
            claims.put("username", e.getUsername());
            String jwt = jwtUtils.generateJwt(claims);
            return new LoginInfo(e.getId(), e.getUsername(), e.getName(), jwt);
        }
        return null;
    }
}
