package com.itheima.aop;

import com.itheima.mapper.OperateLogMapper;
import com.itheima.pojo.LoginInfo;
import com.itheima.pojo.OperateLog;
import com.itheima.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    @Autowired
    OperateLogMapper OperateLogMapper;

    @Around("@annotation(com.itheima.anno.LogOperation)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //记录开始时间
        long start = System.currentTimeMillis();
        //执行目标方法
        Object result = pjp.proceed();
        //记录结束时间
        long end = System.currentTimeMillis();
        //计算耗时
        long costTime = end - start;

        //封装日志数据
        OperateLog operateLog = new OperateLog(
                null,
                getCurrentUserId(),
                LocalDateTime.now(),
                pjp.getTarget().getClass().getName(),
                pjp.getSignature().getName(),
                pjp.getArgs().toString(),
                result.toString(),
                costTime,
                /*getCurrentUserName()*/null
        );
        //保存日志数据到数据库
        OperateLogMapper.insert(operateLog);
        log.info("保存操作日志：{}", operateLog);

        //清空当前线程绑定的id
        CurrentHolder.remove();

        //返回结果
        return result;
    }

    public Integer getCurrentUserId() {
        return CurrentHolder.getCurrentId();
    }

    /*public String getCurrentUserName() {
        return CurrentHolder.getCurrentName();
    }*/
}
