[TOC]

# 用户注册
作为一个现代化电商平台，什么最重要呢？of course 是用户，广大用户群体是支持我们可持续发展的基石，`顾客是上帝`， 虽然在当今上帝已经不被重视了，特别是很多的平台对于老用户就是恨不得赶紧Out...但是用户量是一切的基础，那我们就开始创建我们的上帝吧！

## 创建数据库
---

数据库的部分，我在这里就不多讲了，大家需要的话可以直接去[传送门](https://github.com/Isaac-Zhang/expensive-shop/tree/master/db) 抓取脚本`expensive-shop.sql`.

## 生成UserMapper
---
参考上节内容：[传送门](https://www.cnblogs.com/zhangpan1244/p/11803023.html)

## 编写业务逻辑
---
首先，我们先来分析一下要注册一个用户，我们系统都需要做哪些动作？
![user register](https://i.loli.net/2019/11/06/3tK9mcIWVU6J5Sk.png)
- validate
	- input string(校验输入我们需要通过两个角度处理)
	  - FrontEnd valid
	  > 前端校验是为了降低我们服务器端压力而做的一部分校验，这部分校验可以拦截大多数的错误请求。
	  - Backend valid
	  > 后端校验是为了防止某些不法小伙伴绕开前端从而直接访问我们的api造成数据请求服务器错误，或者前端小伙伴程序有bug...无论是哪一种可能性，都有可能造成严重的后果。
	- email & mobile invalid
  > 因为本人没有追求email / 短信发送服务器，所以这一步就pass，小伙伴们可以自行研究哈。
- control
	- create user
		> 校验通过后，就可以进行创建用户的动作了。
接下来，我们就可以来实际编码实现业务了，我们使用最基本的分层架构，在之前我们已经通过`Mybatis Generator`工具生成了基本的`pojo`,`mapper`，对于简单的操作我们只需要再编写`service`和`controller`层就可以完成我们的开发工作了。

## 编写user service
---

在`mscx-shop-service`中创建`com.liferunner.service.IUserService`接口，包含2个方法`findUserByUserName`和`createUser`，如下：

```java
public interface IUserService {

    /**
     * 根据用户名查询用户是否存在
     *
     * @param username
     * @return
     */
    Users findUserByUserName(String username);

    /**
     * 创建用户
     *
     * @param userRequestDTO 用户请求dto
     * @return 当前用户
     */
    Users createUser(UserRequestDTO userRequestDTO) throws Exception;
}
```

接着，我们需要具体实现这个接口类，如下：
```java
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private final String FACE_IMG = "https://avatars1.githubusercontent.com/u/4083152?s=88&v=4";

    // 构造器注入
    private final UsersMapper usersMapper;
    private final Sid sid;

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, Sid sid) {
        this.usersMapper = usersMapper;
        this.sid = sid;
    }

    @Override
    public Users findUserByUserName(String username) {
        // 构建查询条件
        Example example = new Example(Users.class);
        val condition = example.createCriteria()
                .andEqualTo("username", username);
        return this.usersMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserRequestDTO userRequestDTO) throws Exception {
        log.info("======begin create user : {}=======", userRequestDTO);
        val user = Users.builder()
                .id(sid.next()) //生成分布式id
                .username(userRequestDTO.getUsername())
                .password(MD5GeneratorTools.getMD5Str(userRequestDTO.getPassword()))
                .birthday(DateUtils.parseDate("1970-01-01", "yyyy-MM-dd"))
                .nickname(userRequestDTO.getUsername())
                .face(this.FACE_IMG)
                .sex(SexEnum.secret.type)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        this.usersMapper.insertSelective(user);
        log.info("======end create user : {}=======", userRequestDTO);
        return user;
    }
}
```
这里有几处地方有必要说明一下：
### UserServiceImpl#findUserByUserName 说明

- `tk.mybatis.mapper.entity.Example` 通过使用Example来构建mybatis的查询参数，如果有多个查询条件，可以通过`example.createCriteria().addxxx`逐一添加。

### UserServiceImpl#createUser 说明

- `@Transactional(propagation = Propagation.REQUIRED)`,开启事务，选择事务传播级别为`REQUIRED`,表示必须要有一个事务存在，如果调用者不存在事务，那本方法就自己开启一个新的事物，如果调用方本身存在一个活跃的事务，那本方法就加入到它里面（同生共死）。
- `org.n3r.idworker.Sid`, 这个是一个开源的 分布式ID生成器组件，[传送门](https://github.com/bingoohuang/idworker-client), 后期有机会的话，会专门写一个id生成器文章。
- `MD5GeneratorTools` 是用来对数据进行MD5加密的工具类，大家可以在源码中下载。也可以直接使用`java.security.MessageDigest` 直接加密实现，总之密码不能明文存储就行了。
- `SexEnum` 这个是一个表述性别类型的枚举，在我们编码的规范中，尽量要求不要出现`Magic number`,就是开发界常说的魔术数字（即1,2,300...）
- 这里的日志打印，可能有人会问为什么你没有声明类似:`private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);` ,这是因为我们在开始的时候，我们引入了`lombok`依赖，不记得的同学可以参考[传送门](https://www.cnblogs.com/zhangpan1244/p/11793065.html)。在这里依赖中，它继承了很多的日志组件，我们只需要使用一个注解`lombok.extern.slf4j.Slf4j`来开启日志，使用`log.info..`就可以了。
- `UserRequestDTO` 又是个什么鬼？在我们开发的过程中，很可能会有大批量的参数需要传递，这时我们如果使用`xxx#(String aa,Integer bb,Boolean cc...)`会让我们烦不胜数，而且看着也不美观，这时候我们就可以选择创建一个新对象来帮助我们传递数据，那么也就是我们的`UserRequestDTO`对象，所谓的`DTO`就是`Data Transfer Object`的首字母缩写，顾名思义，它是用来传递数据对象用的。

## 编写user controller
---

同样在`mscx-shop-api`中，创建`com.liferunner.api.controller.UserController`,实现用户创建。

```java
@RestController
@RequestMapping(name = "/users")
@Slf4j
@Api(tags="用户管理")
public class UserController {

    @Autowired
    private IUserService userService;

    @ApiOperation("校验是否重名")
    @GetMapping("/validateUsername")
    public JsonResponse validateUsername(@RequestParam String username) {
        // 判断用户名是否非法
        if (StringUtils.isBlank(username))
            return JsonResponse.errorMsg("用户名不能为空！");
        if (null != userService.findUserByUserName(username))
            return JsonResponse.errorMsg("用户名已存在！");
        // 用户名可用
        return JsonResponse.ok();
    }

    @ApiOperation("创建用户")
    @PostMapping("/create")
    public JsonResponse createUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            if (StringUtils.isBlank(userRequestDTO.getUsername()))
                return JsonResponse.errorMsg("用户名不能为空");
            if (null != this.userService.findUserByUserName(userRequestDTO.getUsername())) {
                return JsonResponse.errorMsg("用户名已存在！");
            }
            if (StringUtils.isBlank(userRequestDTO.getPassword()) ||
                    StringUtils.isBlank(userRequestDTO.getConfimPassword()) ||
                    userRequestDTO.getPassword().length() < 8) {
                return JsonResponse.errorMsg("密码为空或长度小于8位");
            }
            if (!userRequestDTO.getPassword().equals(userRequestDTO.getConfimPassword()))
                return JsonResponse.errorMsg("两次密码不一致！");
            val user = this.userService.createUser(userRequestDTO);
            if (null != user)
                return JsonResponse.ok(user);
        } catch (Exception e) {
            log.error("创建用户失败,{}", userRequestDTO);
        }
        return JsonResponse.errorMsg("创建用户失败");
    }
}
```
### UserController#validateUsername(username) 说明

- `JsonResponse`对象是为了方便返回给客户端一个统一的格式而封装的数据对象。
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResponse {

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();
    // 响应业务状态
    private Integer status;
    // 响应消息
    private String message;
    // 响应中的数据
    private Object data;

    public static JsonResponse build(Integer status, String msg, Object data) {
        return new JsonResponse(status, msg, data);
    }

    public static JsonResponse ok(Object data) {
        return new JsonResponse(data);
    }

    public static JsonResponse ok() {
        return new JsonResponse(null);
    }

    public static JsonResponse errorMsg(String msg) {
        return new JsonResponse(500, msg, null);
    }

    public static JsonResponse errorMap(Object data) {
        return new JsonResponse(501, "error", data);
    }

    public static JsonResponse errorTokenMsg(String msg) {
        return new JsonResponse(502, msg, null);
    }

    public static JsonResponse errorException(String msg) {
        return new JsonResponse(555, msg, null);
    }

    public static JsonResponse errorUserQQ(String msg) {
        return new JsonResponse(556, msg, null);
    }

    public JsonResponse(Object data) {
        this.status = 200;
        this.message = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }
}
```
### UserController#createUser(UserRequestDTO) 说明
- 如上文所讲，需要先做各种校验
- 成功则返回`JsonResponse`
- 细心的同学可能看到了上文中有几个注解`@Api(tags="用户管理")`,`@ApiOperation("创建用户")`,这个是Swagger 的注解，我们会在下一节和大家详细探讨，以及如何生成`off-line docs`。

## 测试API

---
在我们每次修改完成之后，都尽可能的`mvn clean install`一次，因为我们隶属不同的project，如果不重新安装一次，偶尔遇到的问题会让人怀疑人生的。
```shell
...
[INFO] expensive-shop ..................................... SUCCESS [  1.220 s]
[INFO] mscx-shop-common ................................... SUCCESS [  9.440 s]
[INFO] mscx-shop-pojo ..................................... SUCCESS [  2.020 s]
[INFO] mscx-shop-mapper ................................... SUCCESS [  1.564 s]
[INFO] mscx-shop-service .................................. SUCCESS [  1.366 s]
[INFO] mscx-shop-api ...................................... SUCCESS [  4.614 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  20.739 s
[INFO] Finished at: 2019-11-06T14:53:55+08:00
[INFO] ------------------------------------------------------------------------
```
当看到上述运行结果之后，就可以启动我们的应用就行测试啦～

### UserController#validateUsername(username) 测试

测试API的方式有很多种，比如`curl localhost:8080/validateUsername`，在比如使用超级流行的`Postman`也是完全ok的，我这里用的是之前在第一篇中和大家所说的一个插件`Restful Toolkit(可以实现和postman一样的简单效果,同时还能帮助我们生成一部分测试信息)`，当我们应用启动之后，效果如下图，
![rest plugin](https://i.loli.net/2019/11/06/vLFyQY8AkqiZGw3.png)

我们可以看到，插件帮我们生成了几个测试方法，比如我们点击`validateUsername`,下方就会生成当前方法是一个包含`username`参数的`GET`方法，`demoData`是插件默认给我们生成的测试数据。可以随意修改。
点击Send：
![result](https://i.loli.net/2019/11/06/YEyKauUHSqjRc4Z.png)
可以看到请求成功了，并且返回我们自定义的JSON格式数据。

### UserController#createUser(UserRequestDTO)  测试
接着我们继续测试用户注册接口，请求如下：
![send](https://i.loli.net/2019/11/06/QDVkT6dxG8qlcYm.png)
可以看到，当我们选择`create`方法时，插件自动帮我们设置请求类型为`POST`，并且`RequestBody`的默认值也帮助我们生成了，我只修改了默认的`username`和`password`值，`confimPassword`的默认值我没有变动，那按照我们的校验逻辑，它应该返回的是`return JsonResponse.errorMsg("两次密码不一致！");`这一行，点击Send：
![result](https://i.loli.net/2019/11/06/1R5sMFYhGbnkyrd.png)
修改`confimPassword`为`12345678`,点击Send：
![result2](https://i.loli.net/2019/11/06/kPi5mrWJ2zntcws.png)
可以看到，创建用户成功，并且将当前创建的用户返回到了我们请求客户端。那么我们继续重复点击创建，会怎么样呢？继续Send:
![result3](https://i.loli.net/2019/11/06/c8HfT5SExjWK6JX.png)
可以看到，我们的验证重复用户也已经生效啦。

## 下节预告

---
下一节我们将学习如何使用Swagger自动生成API接口文档给前端，以及如果没有外部网络的情况下，或者需要和第三方平台对接的时候，我们如何生成`离线文档`给到第三方。
gogogo！

