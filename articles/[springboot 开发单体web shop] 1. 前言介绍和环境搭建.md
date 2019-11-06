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