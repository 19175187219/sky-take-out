package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillAspect() {
    }
    @Before("autoFillAspect()")
    public void autoFill(JoinPoint joinPoint)  {
log.info("开始:{}");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上注解
        OperationType operationType = annotation.value();//数据库操作类型
        Object[] args = joinPoint.getArgs();
        if (args==null || args.length==0) {return;}
        Object arg = args[0];
        LocalTime now = LocalTime.now();
        Long currentId = BaseContext.getCurrentId();
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreatTime=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, LocalDateTime.class);
                Method setUpdateUser=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, LocalDateTime.class);
setUpdateUser.invoke(arg,currentId);
setUpdateTime.invoke(arg,now);
setCreateUser.invoke(arg,currentId);
setCreatTime.invoke(arg,now);
            } catch (Exception e) {
            }
        }
        else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser=arg.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, LocalDateTime.class);
                setUpdateUser.invoke(arg,currentId);
                setUpdateTime.invoke(arg,now);
            } catch (Exception e) {
            }
        }

    }
}
