# Use springboot to develop a monolithic web application

## 简述

springboot 本身是为了做服务化用的，我们为什么要反其道使用它来开发一份单体web应用呢？
在我们现实的开发工作中，还有大量的业务系统使用的是单体应用，特别是对于中小型团队来说，在项目开发之初选择服务化开发是得不偿失的，因为对于此类团队，势必所有的工作都是需要我们开发人员来做的，例如：

- 技术选型
- 业务需求分析
- 业务需求设计
- 大量的测试
- 运行部署
- 运营健康监控
- ...

小团队或小公司如果开发人员把大规模的精力放在除业务之外的工作中，那么我们的开发效率势必会特别低下，等我们系统开发一个版本出来，也许此类需求已经不是那么符合当前的发展了，此类团队追求的是短平快并且方便部署更新。有人就会问，那我们又为什么不选择`SpringMVC`呢？ 当然如果团队中本身已经有成型的一套SpringMVC的框架可以拿来即用，当然也是完全ok的，但是所有使用过Springboot的同学都知道。

> 1. Springboot帮我们把以前在SpringMVC中需要使用XML来配置的configuration内置化了，开发人员完全可以把大量精力放在业务钻研优化上面而不需要关注它是如何配置的。
> 2. 作为技术选型来说，一定要想到我们的业务也许会高速扩张，在后期我们得能快速更新技术体系或者升级系统，那么springboot的本能服务化就可以体现出来了。
> 3. 很多的新同学在学习springboot的时候，太多的资料本身是一种demo的学习，是需要大家进一步研究之后学以致用，不太贴近生产，我希望通过此次开发，可以直接开发一套可以生产使用的demo系统。

## 业务分析

在开发任何一个`新`系统的时候，我们要实现的是一套电商平台，那么我们首先要考虑一个基本的电商平台都包含哪些业务功能？
![architecture](https://i.loli.net/2019/11/04/arHkDdj4IVm5bAx.png)
上图展示了要实现一个电商最简单的模块信息。

## 技术选型

在技术选型的时候,个人建议遵循几点原则:

1. 切合业务实际需要(任何脱离了业务的技术都是废柴)
2. 团队人员能力(要符合团队成员的实际情况,否则上手会很慢)
3. 技术社区活跃度(选择高活跃的技术对于后期排错相当重要,更为重要的是大部分错误已经被别人试过了...)
4. 安全性(必不可少的选择)
5. 可参考 [Poc之后，我选择放弃OSGI](https://www.cnblogs.com/zhangpan1244/p/11724791.html)

我们主题很明确的表明了要使用`springboot` 来实现一个`web` 项目，那么抓住这两个关键的点。
既然使用Springboot， 我们这里使用最新的版本`2.2.0.RELEASE`, 既然是web项目，那么必然要使用到web相关的技术，`tomcat`(springboot内置)，并且我们采取前后端分离的技术来开发，后端提供restful 的api, 前端使用`jquery` & `vuejs`, 既然是一个真实项目，数据库当然也是我们必不可少的，我们使用`MYSQL 8.0+`,那么我们java要和db进行交互,我们使用`mybatis`作为bridge. 基本的技术已经足够我们使用了,那么具体依赖的package 后续会给大家一一介绍.

- SpringBoot 2.2.0.RELEASE
- Mysql 8.0.18
- Mybatis 3.4.6

## 开发工具

`工预善其事必先利其器`, 以下是个人工具选择,仅供参考:

- IntelliJ IDEA (code tools)
  - Free Mybatis Plugin (帮助我们方法和SQL跳转)
  - Lombok (使用注解节省太多的codes,提升效率)
  - Maven Helper(帮助依赖分析)
  - Restful Toolkit(可以实现和postman一样的简单效果,同时还能帮助我们生成一部分测试信息)
  - ...其他插件就人各有志啦~
- Mysql Workbench(Mysql tools)
- PDman (数据库设计工具,脚本的版本控制很好用哦)

## Talk is cheap, show me the codes

说的再多,都不如来点实在的,那么我们接下来开始我们的表演吧.

### 创建单体项目结构

首先我们需要创建一个Maven的parent module, 用来将我们所有的模块信息都放在一起,如`service`,`controller` 等等.

#### Create Parent Module `expensive-shop`

- 打开IDEA,选择File => New => Project
![create parent module](https://i.loli.net/2019/11/04/ljqDwrLInMASxvu.png)

- 点击Next,分别输入`GroupId` & `artifactId`
![write groupid & artifactid](https://i.loli.net/2019/11/04/mXLHxDyvTbS49uI.png)

- 点击Next,修改`project name`(不改也没有关系)
![修改project name](https://i.loli.net/2019/11/04/1lOwPXHBaLb2vnz.png)

- 点击Finish

此时,会生成`src` 和 `pom.xml`, 因为该项目为父类项目,不会有code实现,因此,删除`src`目录,并修改`pom.xml` 文件.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.0.RELEASE</version>
        <relativePath/>
    </parent>
    <groupId>com.life-runner</groupId>
    <artifactId>expensive-shop</artifactId>
    <version>1.0-SNAPSHOT</version>

    <name>expensive-shop</name>
    <description>develop a on-line shop</description>

    <packaging>pom</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--spring默认使用yml中的配置，有时候要用传统的xml或properties配置，就需要使用spring-boot-configuration-processor-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <!--监控端点依赖-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.8</version>
        </dependency>
    </dependencies>
</project>
```

主要关注2点:
1.修改`packaging` 为pom, 表明是一个父类集合
2.引入`parent`节点为我们的`springboot`,这里是设置整个project的springboot 相关依赖以及版本管控
从上述中,在该节点设置version 为 `2.2.0.RELEASE`之后, 下面所有的`group`为`org.springframework.boot`的依赖项都没有显示的设置版本信息.

#### Create child module

和创建父类项目一样的创建过程,依次创建我们需要用到的child modules, 完成后如下图:
![create child module](https://i.loli.net/2019/11/04/4XVqhjd9lNaxwBz.png)

经过上述环节,相信大家已经可以创建出我们项目所需要的架构环境了,下一次我们将开始实际业务的编码实现.
gogogo.

## Mybatis Generator tool

---
在我们开启一个新项目的研发后，通常要编写很多的`entity/pojo/dto/mapper/dao...`, 大多研发兄弟们都会抱怨，为什么我要重复写`CRUD`? 我们为了避免编写一些不必要的重复代码，这节给大家介绍介绍使用一个开源工具，来帮助我们从这种简单枯燥的编码中解救出来。
隆重有请： [MyBatis通用Mapper4](https://github.com/abel533/Mapper)
> 通用Mapper都可以极大的方便开发人员。可以随意的按照自己的需要选择通用方法，还可以很方便的开发自己的通用方法。
极其方便的使用**MyBatis单表**的增删改查。
支持单表操作，不支持通用的多表联合查询。
通用 Mapper 支持 **Mybatis-3.2.4** 及以上版本。
**Tips:**
各位技术同仁一定要有版本意识哦～
Let's code!

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
    <table tableName="items"></table>
    <table tableName="items_comments"></table>
    <table tableName="items_img"></table>
    <table tableName="items_param"></table>
    <table tableName="items_spec"></table>
    <table tableName="order_items"></table>
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