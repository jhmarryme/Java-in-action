package org.example.springaopmethodauthorization.valid;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.springaopmethodauthorization.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class MethodValidationAspect {

    @Autowired
    private UserService userService; // 注入 UserService 获取动态值

    @Before("@annotation(ValidateMethodField)")
    public void validateMethodField(JoinPoint joinPoint) throws Throwable {
        // 获取当前拦截的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法上注解的详细信息
        ValidateMethodField validateMethodField = method.getAnnotation(ValidateMethodField.class);
        String fieldName = validateMethodField.fieldName();
        String message = validateMethodField.message();

        // 获取方法的参数
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) {
            throw new IllegalArgumentException("方法没有参数，无法校验字段：" + fieldName);
        }
        Object fieldValue = ReflectUtil.findMethodField(method, args, "param.targetField");
        // 获取动态值进行校验
        String dynamicValue = userService.getCurrentUserDynamicValue();

        // 如果校验不通过，抛出异常
        if (!dynamicValue.equals(fieldValue)) {
            throw new IllegalArgumentException(message + "，期望值：" + dynamicValue);
        }
    }


}
