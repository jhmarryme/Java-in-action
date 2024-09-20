## 简介

### auth-center

提供注册/登录接口

完成token的生成

### resource

资源服务器，引入安全配置，通过验证jwt合法性访问

可以使用@PreAuthorize 等方法级别注解实现更细的权限控制

1. 从header中获取access_token（JwtAuthenticationFilter）
2. 验证token合法性（JwtHelper）
3. 通过http请求获取用户信息
4. 构建用户上下文信息

### security

存放需要用到的公共类

## 运行环境

jdk：22

gradle：8.x

## 测试入口

http文件测试:
应用下的resource/http
