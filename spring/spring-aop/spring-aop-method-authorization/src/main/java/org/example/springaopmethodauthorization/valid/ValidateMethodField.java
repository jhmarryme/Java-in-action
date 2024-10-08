package org.example.springaopmethodauthorization.valid;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateMethodField {

    String fieldName(); // 要校验的字段名

    String message() default "字段值不正确"; // 校验失败时的消息
}