package com.itheima.controller;

import com.itheima.pojo.LogQueryParam;
import com.itheima.pojo.OperateLog;
import com.itheima.pojo.PageResult;
import com.itheima.pojo.Result;
import com.itheima.service.OperateLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class OperateLogController {
    @Autowired
    private OperateLogService operateLogService;

    @GetMapping("/page")
    public Result page(LogQueryParam  param) {
        log.info("日志操作分页查询");
        PageResult<OperateLog> list = operateLogService.list(param);
        return Result.success(list);
    }
}
