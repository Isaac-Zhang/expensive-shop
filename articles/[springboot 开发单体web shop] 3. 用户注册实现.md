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

