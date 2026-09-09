package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.mapper.OperateLogMapper;
import com.itheima.pojo.LogQueryParam;
import com.itheima.pojo.OperateLog;
import com.itheima.pojo.PageResult;
import com.itheima.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    private OperateLogMapper operateLogMapper;
    @Override
    public PageResult<OperateLog> list(LogQueryParam param) {
        //设置分页参数
        System.out.println(param.getPage());
        System.out.println(param.getPageSize());
        PageHelper.startPage(param.getPage(), param.getPageSize());
        //获取基础信息
        List<OperateLog> logList = operateLogMapper.list(param);
        //封装结果
        Page<OperateLog> page = (Page<OperateLog>) logList;
        return new PageResult<OperateLog>(page.getTotal(), page.getResult());
    }
}
