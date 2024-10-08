## 简介

> [Java使用XDocReport导出Word（带图片）](https://blog.csdn.net/luck_sheng/article/details/131120863)
>
> [java freemarker + word 模板 生成 word 文档 （变量替换，数据的循环，表格数据的循环，以及图片的替换）](https://www.cnblogs.com/lovling/p/10791139.html)
>
> [springboot使用xdocreport导出word包含图片](https://juejin.cn/post/7265673876032766015)

基于freemarker+xdocreport 向docx模板指定添加图片

最终生成为docx文档

实现方案为图片替换，原docx模板中需要使用图片占位（书签的方式定位）

## 运行环境

jdk：22

gradle：8.x

## 测试入口

http://localhost:8080/generate-docx