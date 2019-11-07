# Swagger生成JavaDoc

---

在日常的工作中，特别是现在前后端分离模式之下，接口的提供造成了我们前后端开发人员的沟通
成本大量提升，因为沟通不到位，不及时而造成的[撕币]事件都成了日常工作。特别是很多的开发人员
不擅长沟通，造成的结果就会让自己特别的痛苦，也让合作人员`恨`的牙根痒痒。
为了结束战火蔓延，同时为了提升开发人员的满意度，`Swagger`应运而生。

## 什么是Swagger

---

> Swagger for Everyone  
> Simplify API development for users, teams, and enterprises with the Swagger open source and professional toolset.  
> Swagger open source and pro tools have helped millions of API developers, teams, and organizations deliver great APIs.

简言之就是指使用工具集简化用户、团队和企业的API开发。  
- [官方传送门](https://swagger.io/)   
- [源码传送门](https://github.com/swagger-api)
- [Swagger-UI传送门](https://github.com/swagger-api/swagger-ui)
- [扩展组件swagger-spring-boot-starter传送门](https://github.com/SpringForAll/spring-boot-starter-swagger)
- [扩展UI组件swagger-bootstrap-ui传送门](https://github.com/xiaoymin/swagger-bootstrap-ui)

## 集成Swagger

---

系统中我选择使用的是`swagger-spring-boot-starter`。
> 该项目主要利用Spring Boot的自动化配置特性来实现快速的将swagger2引入spring boot应用来生成API文档，简化原生使用swagger2的整合代码。
看得出来，我在教大家使用的都是在偷懒哦，这可不是什么好现象。。。

### 添加依赖

```xml
        <!--整合Swagger2-->
        <dependency>
            <groupId>com.spring4all</groupId>
            <artifactId>swagger-spring-boot-starter</artifactId>
            <version>1.9.0.RELEASE</version>
        </dependency>
```
点击版本号进入`swagger-spring-boot-starter/1.9.0.RELEASE/swagger-spring-boot-starter-1.9.0.RELEASE.pom`，可以看到它依赖的版本信息。

```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <version.java>1.8</version.java>
        <version.swagger>2.9.2</version.swagger>
        <version.spring-boot>1.5.10.RELEASE</version.spring-boot>
        <version.lombok>1.18.6</version.lombok>
    </properties>
```

### 启用功能

在我们的启动类`ApiApplication`上增加@EnableSwagger2Doc注解

```java
@SpringBootApplication
@MapperScan(basePackages = "com.liferunner.mapper")
@ComponentScan(basePackages = {"com.liferunner", "org.n3r.idworker"})
@EnableSwagger2Doc //启动Swagger
public class ApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApiApplication.class)
                .run(args);
    }

    @Autowired
    private CORSConfig corsConfig;

    /**
     * 注册跨域配置信息
     *
     * @return {@link CorsFilter}
     */
    @Bean
    public CorsFilter corsFilter() {
        val corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(this.corsConfig.getAllowOrigin());
        corsConfiguration.addAllowedMethod(this.corsConfig.getAllowedMethod());
        corsConfiguration.addAllowedHeader(this.corsConfig.getAllowedHeader());
        corsConfiguration.setAllowCredentials(this.corsConfig.getAllowCredentials());

        val urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
```

### 配置基础信息

可以通过`properties`文件和`yml/yaml`文件配置。

```yaml
# 配置swagger2
swagger:
  enabled: true #是否启用swagger，默认：true
  title: 实战电商api平台
  description: provide 电商 API
  version: 1.0.0.RC
  license: Apache License, Version 2.0
  license-url: https://www.apache.org/licenses/LICENSE-2.0.html
  terms-of-service-url: http://www.life-runner.com
  contact:
    email: magicianisaac@gmail.com
    name: Isaac-Zhang
    url: http://www.life-runner.com
  base-package: com.liferunner
  base-path: /**
```
### 阶段效果一

运行我们的api项目，在浏览器输入：`http://localhost:8088/swagger-ui.html`,可以看到如下：
![阶段效果1](https://i.loli.net/2019/11/07/iP5WwHomJlNFQvf.png)
可以看到，我们在`yml`文件中配置的信息，展示在了页面的顶部，点击`用户管理`:
![用户管理](https://i.loli.net/2019/11/07/R5tIGmJw3aeTdfu.png)
从上图可以看出，我们的`/users/create`接口展出出来，并且要传入的参数，请求类型等等信息都已经展示在上图中。
但是，要传递的参数是什么意思，都是我们的字段信息，我们要如何让它更友好的展示给调用方呢？让我们继续
完善我们的文档信息：

### 完善说明信息

在我们创建用户的时候，需要传递一个`com.liferunner.dto.UserRequestDTO`对象，这个对象的属性如下：

```java
@RestController
@RequestMapping(value = "/users")
@Slf4j
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户详情", notes = "查询用户")
    @ApiIgnore
    @GetMapping("/get/{id}")
    //@GetMapping("/{id}") 如果这里设置位这样，每次请求swagger都会进到这里，是一个bug
    public String getUser(@PathVariable Integer id) {
        return "hello, life.";
    }

    @ApiOperation(value = "创建用户", notes = "用户注册接口")
    @PostMapping("/create")
    public JsonResponse createUser(@RequestBody UserRequestDTO userRequestDTO) {
        //...
    }
}
```

---

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "创建用户DTO", description = "用户注册需要的参数对象")
public class UserRequestDTO {
    @ApiModelProperty(value = "用户名", notes = "username", example = "isaaczhang", required = true)
    private String username;
    @ApiModelProperty(value = "注册密码", notes = "password", example = "12345678", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", notes = "confimPassword", example = "12345678", required = true)
    private String confirmPassword;
}
```
可以看到，我们有很多通过`@Apixxx`开头的注解说明，这个就是Swagger提供给我们用以说明字段和文档说明的注解。
- `@Api` 表示对外提供API
- `@ApiIgnore` 表示不对外展示，可用于类和方法
- `@ApiOperation` 就是指的某一个API下面的CURD动作
- `@ApiResponses` 描述操作可能出现的异常情况
- `@ApiParam` 描述传递的单参数信息
- `@ApiModel` 用来描述java object的属性说明
- `@ApiModelProperty` 描述object 字段说明
所有的使用，都可以进入到相关的注解的具体class去查看所有的属性信息，都比较简单，这里就不做具体描述了。想要查看更多的属性说明，
大家可以进入：[Swagger属性说明传送门](http://docs.swagger.io/swagger-core/apidocs/index.html)。

配置完之后，重启应用，刷新UI页面：
![阶段效果二](https://i.loli.net/2019/11/07/LeQxbMCNuZTgEOp.png)
上图中红框圈定的都是我们重新配置的说明信息，足够简单明了吧～

## 集成更好用的UI界面
针对于API说明来说，我们上述的信息已经足够优秀了，可是做技术，我们应该追求的是更加极致的地步，上述的UI界面在我们提供大批量
用户接口的时候，友好型就有那么一丢丢的欠缺了，现在给大家再介绍一款更好用的开源`Swagger UI`，有请[swagger-bootstrap-ui](https://github.com/xiaoymin/swagger-bootstrap-ui)。
![UI2](https://i.loli.net/2019/11/07/QuZn4SzbH96lvKm.png)
我们从上图可以看到，这个UI的Star数目已经超过1.1K了，这就证明它已经很优秀了，我们接下来解开它的庐山真面目吧。

### 集成依赖

只需要在我们的`expensive-shop\pom.xml`中加入以下依赖代码：

```xml
        <!--一种新的swagger ui-->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>swagger-bootstrap-ui</artifactId>
            <version>1.6</version>
        </dependency>
```

### 预览效果

添加完依赖后，只需要重启我们的应用，然后访问`http://localhost:8088/doc.html`,效果如下：
![阶段效果3](https://i.loli.net/2019/11/07/rTVbyeWD2N68Ou5.png)
点击创建用户：
![阶段效果4](https://i.loli.net/2019/11/07/a8URLxV2NQpeMvg.png)
上述的效果是不是更符合我们的审美了～
到此为止，我们使用`Swagger`来动态生成API的效果已经全部演示完了，但是如果某一天我们要和一个不能连接查看我们网站的客户进行集成的时候，我们怎么办呢？
还是要手写一份文档给他们吗？ 那我们不就一样很痛苦吗！！！
作为程序员，我们是绝对不能允许这种情况发生的！
那就让我们继续看下去。

## 生成离线文档
为了不让我们做痛苦的工作，我们既然已经在代码中添加了那么多的说明信息，是否有一种方式可以帮助我们来生成一份离线的文档呢？答案是肯定的。

### 开源项目swagger2markup
> A Swagger to AsciiDoc or Markdown converter to simplify the generation of an up-to-date RESTful API documentation by combining documentation that’s been hand-written with auto-generated API documentation.

[源码传送门](https://github.com/Swagger2Markup/swagger2markup)  
[documents传送门](http://swagger2markup.github.io/swagger2markup/1.3.3/)
> Swagger2Markup它主要是用来将Swagger自动生成的文档转换成几种流行的格式以便离线使用  
> 格式：AsciiDoc、HTML、Markdown、Confluence

### 使用MAVEN插件生成AsciiDoc文档
在`mscx-shop-api\pom.xml`中加入以下依赖代码：

```xml
<build>
        <plugins>
            <!--生成 AsciiDoc 文档(swagger2markup)-->
            <plugin>
                <groupId>io.github.swagger2markup</groupId>
                <artifactId>swagger2markup-maven-plugin</artifactId>
                <version>1.3.3</version>
                <configuration>
                    <!--这里是要启动我们的项目，然后抓取api-docs的返回结果-->
                    <swaggerInput>http://localhost:8088/v2/api-docs</swaggerInput>
                    <outputDir>src/docs/asciidoc/generated-doc</outputDir>
                    <config>
                        <swagger2markup.markupLanguage>ASCIIDOC</swagger2markup.markupLanguage>
                    </config>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
- `<swaggerInput>http://localhost:8088/v2/api-docs</swaggerInput>` 是为了获取我们的`api JSON`数据，如下图：
![API-JSON](https://i.loli.net/2019/11/07/6ZE3bBISUJfcPQK.png)
- `<outputDir>src/docs/asciidoc/generated-doc</outputDir>` 设置我们要生成的目录地址

执行命令:
```shell script
expensive-shop\mscx-shop-api>mvn swagger2markup:convertSwagger2markup
```
要是大家觉得命令太长了，也可以点击`IDEA => Maven => mscx-shop-api => Plugins => swagger2markup => swagger2markup:convertSwagger2markup
`就可以执行啦，如下图：
![swagger2markup](https://i.loli.net/2019/11/07/E2yR4lhQU1s65Zj.png)
生成结果如下：
![asciidoc](https://i.loli.net/2019/11/07/Bk2IpZEJP6KhMli.png)
adoc文件生成好了，那么我们使用它来生成html吧

### 使用MAVEN插件生成HTML

在`mscx-shop-api\pom.xml`中加入以下依赖代码：

```xml
            <!--生成 HTML 文档-->
            <plugin>
                <groupId>org.asciidoctor</groupId>
                <artifactId>asciidoctor-maven-plugin</artifactId>
                <version>1.5.6</version>
                <configuration>
                    <sourceDirectory>src/docs/asciidoc/generated-doc</sourceDirectory>
                    <outputDirectory>src/docs/asciidoc/html</outputDirectory>
                    <backend>html</backend>
                    <sourceHighlighter>coderay</sourceHighlighter>
                    <attributes>
                        <toc>left</toc>
                    </attributes>
                </configuration>
            </plugin>
```

- `<sourceDirectory>src/docs/asciidoc/generated-doc</sourceDirectory>` 源文件目录指定为我们上一节生成的adoc
- `<outputDirectory>src/docs/asciidoc/html</outputDirectory>` 指定输出目录

执行生成命令：

```shell script
\expensive-shop\mscx-shop-api>mvn asciidoctor:process-asciidoc
```
生成结果如下：
![result html](https://i.loli.net/2019/11/07/qUSlhM3ufpkLaYe.png)
打开`overview.html`,如下：
![html](https://i.loli.net/2019/11/07/dpqjf6LngFeoaTh.png)

至此，我们的文档就已经全部生成了！

## 下节预告

---

下一节我们将继续开发我们的用户登录以及首页信息的部分展示，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！

