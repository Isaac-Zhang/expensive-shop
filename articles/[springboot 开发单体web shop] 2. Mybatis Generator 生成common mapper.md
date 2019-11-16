# Use springboot to develop a monolithic web application

### Create mybatis-generator-tool Module

参考[上一节](https://www.cnblogs.com/zhangpan1244/p/11793065.html#4415077)中的Module创建`mybatis-generator-tool`.

---

- 添加依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>expensive-shop</artifactId>
        <groupId>com.life-runner</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>mybatis-generator-tool</artifactId>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <!--springboot 构建可执行fat jars必须的插件，如不添加，在生产环境会有问题-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.mybatis.generator</groupId>
                <artifactId>mybatis-generator-maven-plugin</artifactId>
                <version>1.3.6</version>
                <configuration>
                    <!-- 设置配置文件路径 -->
                    <configurationFile>
                        ${basedir}/src/main/resources/generator/generatorConfig.xml
                    </configurationFile>
                    <!--允许覆盖-->
                    <overwrite>true</overwrite>
                    <verbose>true</verbose>
                </configuration>
                <dependencies>
                    <!-- mysql8 驱动-->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>8.0.16</version>
                    </dependency>
                    <!--通用 Mapper-->
                    <dependency>
                        <groupId>tk.mybatis</groupId>
                        <artifactId>mapper</artifactId>
                        <version>4.1.5</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
</project>
```

---

- 编写配置文件
根据我们在pom文件中指定的路径：`${basedir}/src/main/resources/generator/generatorConfig.xml`, 我们需要在项目`src=>main=>resource`目录下创建`generator`文件夹，在文件夹下创建文件`generatorConfig.xml`,内容如下：

```XML
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
  <!--引入数据库配置内容-->
  <properties resource="generator/config.properties"/>

  <context id="MysqlContext" targetRuntime="MyBatis3Simple" defaultModelType="flat">
    <!--配置是否使用通用 Mapper 自带的注释扩展，默认 true-->
    <!--<property name="useMapperCommentGenerator" value="false"/>-->

    <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
      <!--设置Mapper生成的basic,可自定义-->
      <property name="mappers" value="tk.mybatis.mapper.common.Mapper"/>
      <!--大小写转换敏感-->
      <property name="caseSensitive" value="true"/>
      <!--引入lombok注解-->
      <property name="lombok" value="Getter,Setter,ToString"/>
      <!--分隔符定义-->
      <property name="beginningDelimiter" value="`"/>
      <property name="endingDelimiter" value="`"/>
    </plugin>

    <!-- 设置数据库配置 -->
    <jdbcConnection driverClass="${jdbc.driverClass}"
      connectionURL="${jdbc.url}"
      userId="${jdbc.user}"
      password="${jdbc.password}">
    </jdbcConnection>

    <!-- 对应生成的pojo所在包 -->
    <javaModelGenerator targetPackage="com.liferunner.pojo" targetProject="src/main/java"/>

    <!-- 对应生成的mapper所在目录 -->
    <sqlMapGenerator targetPackage="mapper" targetProject="src/main/resources"/>

    <!-- 配置mapper对应的java映射 -->
    <javaClientGenerator targetPackage="com.liferunner.mapper" targetProject="src/main/java" type="XMLMAPPER"/>

    <!-- 数据库表 -->
    <table tableName="carousel"></table>
    <table tableName="category"></table>
    <table tableName="products"></table>
    <table tableName="products_comments"></table>
    <table tableName="products_img"></table>
    <table tableName="products_param"></table>
    <table tableName="products_spec"></table>
    <table tableName="order_products"></table>
    <table tableName="order_status"></table>
    <table tableName="orders"></table>
    <table tableName="shop_users"></table>
    <table tableName="user_address"></table>
    <table tableName="users"></table>
  </context>
</generatorConfiguration>
```

我们可以看到一行配置内容：`<properties resource="generator/config.properties"/>`,这里是为了将我们的数据库连接、账号等信息外置，配置内容如下：

```properties
jdbc.driverClass = com.mysql.cj.jdbc.Driver
jdbc.url = jdbc:mysql://localhost:3306/expensiveshop?characterEncoding=UTF-8&useSSL\
  =false&useUnicode=true&serverTimezone=UTC
jdbc.user = root
jdbc.password = 12345678
```

可以看到这里设置的内容就是下属代码中用到的。

```xml
...
   <jdbcConnection driverClass="${jdbc.driverClass}"
      connectionURL="${jdbc.url}"
      userId="${jdbc.user}"
      password="${jdbc.password}">
    </jdbcConnection>
...
```

配置信息大家可以参考:[传送门](https://github.com/abel533/Mapper/wiki/4.2.codegenerator)

---

- 使用maven测试生成
执行以下命令：

```shell
mybatis-generator-tool>mvn mybatis-generator:generate
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------< com.life-runner:mybatis-generator-tool >---------------
[INFO] Building mybatis-generator-tool 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- mybatis-generator-maven-plugin:1.3.6:generate (default-cli) @ mybatis-generator-tool ---
[INFO] Connecting to the Database
[INFO] Introspecting table carousel
[INFO] Introspecting table category
...
[INFO] Generating Record class for table carousel
[INFO] Generating Mapper Interface for table carousel
[INFO] Generating SQL Map for table carousel
...
[INFO] Saving file CarouselMapper.xml
...
[INFO] Saving file Carousel.java
[INFO] Saving file Users.java
...
[WARNING] Table configuration with catalog null, schema null, and table shop_users did not resolve to any tables
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.374 s
[INFO] Finished at: 2019-11-05T15:40:07+08:00
[INFO] ------------------------------------------------------------------------

```

可以看到执行成功，虽然这里执行成功，但是当我们打开文件的时候会发现：

```java
package com.liferunner.pojo;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "Carousel")
public class Carousel {
    /**
     * ����id �û�id
     */
    @Id
    private String id;

    /**
     * �û��� �û���
     */
    private String imageUrl;
    ...
}
```

这里出现了乱码问题，这又是怎么回事呢？
没关系，let's bing... [传送门](https://cn.bing.com/search?q=mybatis+generator+%E7%94%9F%E6%88%90%E6%B3%A8%E9%87%8A%E4%B9%B1%E7%A0%81&qs=n&form=QBRE&sp=-1&pq=mybatis+generator+%E7%94%9F%E6%88%90%E6%B3%A8%E9%87%8A%E4%B9%B1%E7%A0%81&sc=0-24&sk=&cvid=6C3157BC96D3468E830CF5F42ACA29A6)，可以看到有265000条结果，那就说明我们的问题已经有太多的人遇到了，随便点开一个：
![bug1](https://i.loli.net/2019/11/05/2S97lZp5M1XhPQY.png)
可以看到红框里面的内容我们缺失了，在`\expensive-shop\mybatis-generator-tool\src\main\resources\generator\generatorConfig.xml`中添加上 `<property name="javaFileEncoding" value="UTF-8"/>`,重新执行生成命令，可以看到我们的乱码就没有了。

```java
@Getter
@Setter
@ToString
@Table(name = "`carousel`")
public class Carousel {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 图片 图片地址
     */
    @Column(name = "`image_url`")
    private String imageUrl;
    ...
```

> Tips: 
> 在这一环节先剧透一个bug，否则我担心在后续大家遇到的时候，因为它确实是和Common Mapper生成相关的。

我们点开生成的`Users.java`,可以看到如下所示：

```java
@Getter
@Setter
@ToString
@Table(name = "users")
public class Users {
    @Column(name = "USER")
    private String user;

    @Column(name = "CURRENT_CONNECTIONS")
    private Long currentConnections;

    @Column(name = "TOTAL_CONNECTIONS")
    private Long totalConnections;
}
```

可是我们的`Users`表不是这样的呀，这是怎么回事？？？
让我们分析分析：
1.既然没有用到我们自己的Users表，但是又确实通过生成器生成了，那么很明显肯定是Mysql数据库中表，这是肯定的。
2.那么问题就来了，它从哪里冒出来的？找它，盘它。
3.到底是哪个数据库中的呢？sys？information_schema？performance_schema？
4.挨个查询，果然：
![bug2](https://i.loli.net/2019/11/05/AZNDwaGEkCcJf2H.png)
可以看到，在`performance_schema`数据库中有一个`users`表，那么到底是不是我们生成出来的呢？执行`SHOW CREATE TABLE users`, 结果如上图，字段和生成出来的是一致的！
5.抓住它了，怎么盘它？？？
> 很简单，修改jdbc:mysql://localhost:3306/expensiveshop?**nullCatalogMeansCurrent=true**&characterEncoding=UTF-8&useSSL\
  =false&useUnicode=true&serverTimezone=UTC，新增上加粗部分就可以了。

`nullCatalogMeansCurrent` 字面意思很简单，就是说如果是null catalog,我就选择current.因为`mysql不支持catalog`,我们需要告知`mybatis`这个特性，设置为`true`就行了。
> 按照SQL标准的解释，在SQL环境下Catalog和Schema都属于抽象概念，主要用来解决命名冲突问题。
从概念上说，一个数据库系统包含多个Catalog，每个Catalog又包含多个Schema，而每个Schema又包含多个数据库对象（表、视图、序列等），反过来讲一个数据库对象必然属于一个Schema，而该Schema又必然属于一个Catalog，这样我们就可以得到该数据库对象的完全限定名称从而解决命名冲突的问题了
从实现的角度来看，各种数据库系统对Catalog和Schema的支持和实现方式千差万别，针对具体问题需要参考具体的产品说明书，比较简单而常用的实现方式是使用数据库名作为Catalog名，Oracle使用用户名作为Schema名.

![bug2-1](https://i.loli.net/2019/11/05/tk6PfVQuZMpYHKx.png)

可查阅Mysql官网说明:[传送门](https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-configuration-properties.html)

本节我们讲解了如何生成我们想要的，简单和重要又重复的工作我们可以通过工具实现啦，下一次我们将开始实际业务的编码实现.
gogogo.