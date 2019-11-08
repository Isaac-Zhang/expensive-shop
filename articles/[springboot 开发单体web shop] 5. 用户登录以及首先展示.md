# 用户登录及首先展示

---

## 用户登录

在之前的文章中我们实现了用户注册和验证功能，接下来我们继续实现它的登录，以及登录成功之后要在页面上显示的信息。
接下来，我们来编写代码。

---

### 实现service

在`com.liferunner.service.IUserService`接口中添加用户登录方法：

```java
public interface IUserService {
    ...
    /**
     * 用户登录
     * @param userRequestDTO 请求dto
     * @return 登录用户信息
     * @throws Exception
     */
    Users userLogin(UserRequestDTO userRequestDTO) throws Exception;
}
```

然后，在`com.liferunner.service.impl.UserServiceImpl`实现类中实现：

```java
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    ...
    @Override
    public Users userLogin(UserRequestDTO userRequestDTO) throws Exception {
        log.info("======用户登录请求：{}", userRequestDTO);
        Example example = new Example(Users.class);
        val condition = example.createCriteria();
        condition.andEqualTo("username", userRequestDTO.getUsername());
        condition.andEqualTo("password", MD5GeneratorTools.getMD5Str(userRequestDTO.getPassword()));
        val user = this.usersMapper.selectOneByExample(example);
        log.info("======用户登录处理结果：{}", user);
        return user;
    }
}
```

**Error Tips：**
这里有一个小小的`坑点`，大家一定要注意，在使用`selectOneByExample()`查询的时候,该方法传入的参数一定注意是`tk.mybatis.mapper.entity.Example`实例，而不是`tk.mybatis.mapper.entity.Example.Criteria`,否则会报动态SQL生成查询错误，信息如下：

```shell
org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named 'distinct' in 'class tk.mybatis.mapper.entity.Example$Criteria'
  at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:92)
  at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:440)
  at com.sun.proxy.$Proxy106.selectOne(Unknown Source)
  at org.mybatis.spring.SqlSessionTemplate.selectOne(SqlSessionTemplate.java:159)
  at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:87)
  at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:93)
  at com.sun.proxy.$Proxy109.selectOneByExample(Unknown Source)
  at com.liferunner.service.impl.UserServiceImpl.userLogin(UserServiceImpl.java:80)
  ...
```

新人在写代码的时候，特别容易在上一行写了查询变量，下一行就直接开用了，越是简单的错误越是让人无从下手。

### 实现Controller

```java
@RestController
@RequestMapping(value = "/users")
@Slf4j
@Api(tags = "用户管理")
public class UserController {
    ...
    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/login")
    public JsonResponse userLogin(@RequestBody UserRequestDTO userRequestDTO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(userRequestDTO.getUsername()))
                return JsonResponse.errorMsg("用户名不能为空");
            if (StringUtils.isBlank(userRequestDTO.getPassword()) ||
                    userRequestDTO.getPassword().length() < 8) {
                return JsonResponse.errorMsg("密码为空或长度小于8位");
            }
            val user = this.userService.userLogin(userRequestDTO);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(user, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                        JSON.toJSONString(userResponseDTO), true);
                return JsonResponse.ok(userResponseDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户登录失败,{},exception = {}", userRequestDTO, e.getMessage());
        }
        return JsonResponse.errorMsg("用户登录失败");
    }
}
```

在上面的代码中，基本校验问题就不再赘述，我们主要关注几点新的特性信息：

- `com.liferunner.dto.UserResponseDTO` 将我们需要展示给前端的数据封装为一个新的返回对象，我们从数据库中查询出来的`Users`pojo包含用户的所有数据，比如其中的`password`、`mobile`等等一些用户私密的数据是不应该展示给前端的，即便要展示，那也是需要经过脱敏以及加密。因此，常见的做法就是封装一个新的返回对象，其中只需要包含前端需要的数据字段就可以了。

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户信息返回DTO", description = "用户登录成功后需要的返回对象")
public class UserResponseDTO {
    /**
     * 主键id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String face;

    /**
     * 性别  1:男  0:女  2:保密
     */
    private Integer sex;
}
```

在这里建议大家使用`Ctrl+C`我们的`com.liferunner.pojo.Users`对象，然后删除掉我们不需要的字段就可以了，为什么这么`建议`呢，是因为下一个好处啦。

- `org.springframework.beans.BeanUtils.copyProperties(user, userResponseDTO);`  
  大家可以看到，这里直接使用的是`Spring BeanUtils`工具类进行的值拷贝，就减少了我们循环遍历每一个字段去挨个赋值`(SetValue)`的工作。（也是一种偷懒小技巧哦，这样是不对的～）

- `CookieTools.setCookie();`  
  之前我们有提过，一般情况下，我们用户登录之后，数据都会被存储在本地浏览器`Cookie`中,比如我登录的`baidu.com`:
  ![baidu](https://i.loli.net/2019/11/08/oef8glcCK1qVtRX.png)
此时，鼠标在图片中左侧的`Cookies => www.baidu.com`右键`clear`,然后再次刷新我们当前界面，效果如下：
![clear cookies](https://i.loli.net/2019/11/08/Ncmtvad94YEkRTP.png)
我们可以看到，从登录状态已经变为退出状态了，并且`Cookies`中的内容也少了很多，这就说明，百度是把我们的用户登录信息加密后存储在了浏览器cookie中。
大家可以查看京东，淘宝等等，也是基于这种方式实现的，开篇之初就说过，我们的系统是基于生产来实现的demo,那么我们就是用主流的实现方法来做。当然，有的同学会说，这个应该我们把数据传递给前端，让前端来实现的！！！当然，你说的对，可是我们掌握一种实现方式，对于我们个人而言应该是没有坏处的吧？
这里就需要一个工具类了，大家可以在[github传送门](https://github.com/Isaac-Zhang/expensive-shop)来下载相关代码。目录`com.liferunner.utils.CookieTools`.

- `com.alibaba.fastjson.JSON.toJSONString(userResponseDTO)`  
  因为我们要返回的是一个对象，但是`cookie`中我们需要放入的是`String`,这里我们引入了alibaba的JSON工具，在`mscx-shop-common/pom.xml`,加入依赖：

  ```xml
      <dependencies>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.56</version>
        </dependency>
    </dependencies>
  ```

## 源码下载

[Github 传送门](https://github.com/Isaac-Zhang/expensive-shop)  
[Gitee 传送门](https://gitee.com/IsaacZhang/expensive-shop)

## 下节预告

---

下一节我们将继续开发我们的用户登录以及首页信息的部分展示，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！
