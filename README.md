# swagger-ui.html 生成本地版本

* 加载jar。 在application中添加 @EnableSwagger2UiLocal 注解

* 下载配置的json文件
1.  可以请求url：http://xxxx/swagger/api-docs 下载json文件
```
  参数可以带groupName 。 默认default
```

2. 配置生成文件的磁盘路径
swagger-filePath=E:\\


## 样例图片
<img url="https://raw.githubusercontent.com/micrqwe/swagger-ui-boot-local/master/src/test/java/1.png">