package com.itheima.exception;

import com.itheima.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.DuplicateFormatFlagsException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理全局异常
    @ExceptionHandler
    public Result handleException(Exception e){
        log.error("服务器异常：{}",e);
        return Result.error("服务器异常，请联系管理员");
    }

    //处理数据重复异常
    @ExceptionHandler
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        log.error("数据重复异常：{}",e.getMessage());
        String msg = e.getMessage();
        int i = msg.indexOf("Duplicate entry");
        String[] arr = msg.substring(i).split(" ");
        return Result.error(arr[2] + "已存在");
    }

    //处理表格关联错误
    @ExceptionHandler
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException e){
        //异常层org.springframework.dao.捕获异常（DataIntegrityViolationException）
//        log.error("表格关联异常：{}",e.getMessage());
        String msg = e.getMessage();
        int i = msg.indexOf("REFERENCES");
        String[] arr = msg.substring(i).split(" ");
        if (arr[1].equals("`clazz`"))
            return Result.error("对不起，当前班级下有学生，不能直接删除！");
        else if (arr[1].equals("`dept`"))
            return Result.error("对不起，当前部门下有员工，不能直接删除！");

        return Result.error("该记录有数据关联，不能删除");
    }
}
