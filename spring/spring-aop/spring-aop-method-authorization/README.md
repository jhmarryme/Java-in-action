## 简介

动态参数校验，要求参数的某个指定字段（支持嵌套指定如 user.name.prefix）必须为xx值（该值可以动态获取，根据用户身份查询获取）

### 核心类

ReflectUtil：通过反射获取方法参数中的指定字段

MethodValidationAspect：校验入口

## 运行环境

jdk：22

gradle：8.x

## 测试入口
