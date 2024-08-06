## 简介

[spring仓库的官方demo](https://github.com/spring-projects/spring-security-samples/blob/6.3.x/servlet/spring-boot/java/oauth2/authorization-server/build.gradle)

只改动了build.gradle使其能本地运行，分支为6.3.x

authorization-server的拓展自定义授权类型demo

## 运行环境

jdk：22

gradle：8.x

## 测试入口

集成测试入口：
src/test/java/org/example/authorizationserver/integTest/AuthorizationServerCustomGrantTypeApplicationTests.java