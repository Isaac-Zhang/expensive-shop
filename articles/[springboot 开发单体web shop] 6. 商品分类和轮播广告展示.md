# 商品分类&轮播广告

---

*因最近又被困在了OSGI技术POC，更新进度有点慢，希望大家不要怪罪哦。*

[上节](https://segmentfault.com/a/1190000020972616) 我们实现了登录之后前端的展示，如：
![登录展示效果](https://i.loli.net/2019/11/18/Nvjg9AEnxFrGheZ.png)
![子分类](https://i.loli.net/2019/11/18/dGW3KJZzTuCxRgN.png)

接着，我们来实现左侧分类栏目的功能。

## 商品分类|ProductCategory
---

从上图我们可以看出，商品的分类其实是有层级关系的，而且这种关系一般都是无限层级。在我们的实现中，为了效果的展示，我们仅仅是展示3级分类，在大多数的中小型电商系统中，三级分类完全足够应对SKU的分类。

### 需求分析
---

先来分析分类都包含哪些元素，以`jd`为例：
![京东分类](https://i.loli.net/2019/11/18/4BPeTLQo82G1yrf.png)

- logo(logo) 有的分类文字前面会有小标
- 分类展示主图(img_url)
- 主标题(title)
- 副标题/Slogan
- 图片跳转地址（img_link_url）-- 大多数时候我们点击分类都会`分类Id`跳转到固定的分类商品列表展示页面，但是在一些特殊的场景，比如我们要做一个活动，希望可以点击某一个分类的主图直接定位到活动页面，这个url就可以使用了。
- 上级分类(parent_id)
- 背景色(bg_color)
- 顺序（sort）
- 当前分类级别（type）

### 开发梳理
---

在上一小节，我们简单分析了一下要实现商品分类的一些`points`，那么我们最好在每次拿到需求【开发之前】，对需求进行拆解，然后分解开发流程，这样可以保证我们更好的理解需求，以及在开发之前发现一部分不合理的需求，并且如果需求设计不合理的话，开发人员完全有权，也有责任告知PM。大家的终极目的都是为了我们做的产品更加合理，好用，受欢迎！
- 首次展示，仅仅读取一级分类（Root）
- 根据一级分类查询二三级子分类

### 编码实现
---

#### 查询一级分类
##### Service实现
1.在`com.liferunner.service`中创建service 接口`ICategoryService.java`, 编写查询所有一级分类的方法`getAllRootCategorys`，如下：

```java
package com.liferunner.service;
import com.liferunner.dto.CategoryResponseDTO;
import com.liferunner.dto.SecondSubCategoryResponseDTO;
import java.util.List;
/**
 * ICategoryService for : 分类service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/13
 */
public interface ICategoryService {
    /**
     * 获取所有有效的一级分类（根节点）
     *
     * @return
     */
    List<CategoryResponseDTO> getAllRootCategorys();
}
```

2.编写实现类`com.liferunner.service.ICategoryService.java`

```java
@Service
@Slf4j
public class CategorySericeImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    public List<CategoryResponseDTO> getAllRootCategorys() {
        Example example = new Example(Category.class);
        val conditions = example.createCriteria();
        conditions.andEqualTo("type", CategoryTypeEnum.ROOT.type);
        val categoryList = this.categoryMapper.selectByExample(example);
        //声明返回对象
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            //赋值
            CategoryResponseDTO dto;
            for (Category category : categoryList) {
                dto = new CategoryResponseDTO();
                BeanUtils.copyProperties(category, dto);
                categoryResponseDTOS.add(dto);
            }
        }
        return categoryResponseDTOS;
    }
}
```

上述代码很好理解，创建`tk.mybatis.mapper.entity.Example`，将条件传入，然后使用`通用Mapper`查询到`type=1`的一级分类，接着将查到的对象列表转换为DTO对象列表。

##### Controller实现
一般情况下，此类查询都会出现在网站的首页，因此我们来创建一个`com.liferunner.api.controller.IndexController`,并对外暴露一个查询一级分类的接口：

```java
package com.liferunner.api.controller;

import com.liferunner.service.ICategoryService;
import com.liferunner.service.IProductService;
import com.liferunner.service.ISlideAdService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * IndexController for : 首页controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页信息controller", tags = "首页信息接口API")
@Slf4j
public class IndexController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/rootCategorys")
    @ApiOperation(value = "查询一级分类", notes = "查询一级分类")
    public JsonResponse findAllRootCategorys() {
        log.info("============查询一级分类==============");
        val categoryResponseDTOS = this.categoryService.getAllRootCategorys();
        if (CollectionUtils.isEmpty(categoryResponseDTOS)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============一级分类查询result：{}==============", categoryResponseDTOS);
        return JsonResponse.ok(categoryResponseDTOS);
    }
}
```

##### Test API
编写完成之后，我们需要对我们的代码进行测试验证，还是通过使用`RestService`插件来实现，当然，大家也可以通过Postman来测试。

```json
{
  "status": 200,
  "message": "OK",
  "data": [
    {
      "id": 1,
      "name": "烟酒",
      "type": 1,
      "parentId": 0,
      "logo": "img/cake.png",
      "slogan": "吸烟受害健康",
      "catImage": "http://www.life-runner.com/shop/category/cake.png",
      "bgColor": "#fe7a65"
    },
    {
      "id": 2,
      "name": "服装",
      "type": 1,
      "parentId": 0,
      "logo": "img/cookies.png",
      "slogan": "我选择我喜欢",
      "catImage": "http://www.life-runner.com/shop/category/cookies.png",
      "bgColor": "#f59cec"
    },
    {
      "id": 3,
      "name": "鞋帽",
      "type": 1,
      "parentId": 0,
      "logo": "img/meat.png",
      "slogan": "飞一般的感觉",
      "catImage": "http://www.life-runner.com/shop/category/meat.png",
      "bgColor": "#b474fe"
    }
  ],
  "ok": true
}
```

#### 根据一级分类查询子分类
因为根据一级id查询子分类的时候，我们是在同一张表中做自连接查询，因此，通用mapper已经不适合我们的使用，因此我们需要自定义mapper来实现我们的需求。

##### 自定义Mybatis Mapper实现
在之前的编码中，我们都是使用的插件帮我们实现的通用`Mapper`,但是这种查询只能处理简单的单表`CRUD`，一旦我们需要SQL 包含一部分逻辑处理的时候，那就必须得自己来编写了，let's code.
1.在项目`mscx-shop-mapper`中，创建一个新的`custom package`,在该目录下创建自定义mapper`com.liferunner.custom.CategoryCustomMapper`。

```java
public interface CategoryCustomMapper {
    List<SecondSubCategoryResponseDTO> getSubCategorys(Integer parentId);
}
```

2.`resources`目录下创建目录`mapper.custom`,以及创建和上面的接口相同名称的`XML`文件`mapper/custom/CategoryCustomMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.liferunner.custom.CategoryCustomMapper">
    <resultMap id="subCategoryDTO" type="com.liferunner.dto.SecondSubCategoryResponseDTO">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <result column="name" jdbcType="VARCHAR" property="name"/>
        <result column="type" jdbcType="INTEGER" property="type"/>
        <result column="parentId" jdbcType="INTEGER" property="parentId"/>
        <collection property="thirdSubCategoryResponseDTOList" ofType="com.liferunner.dto.ThirdSubCategoryResponseDTO">
            <id column="subId" jdbcType="INTEGER" property="subId"/>
            <result column="subName" jdbcType="VARCHAR" property="subName"/>
            <result column="subType" jdbcType="INTEGER" property="subType"/>
            <result column="subParentId" jdbcType="INTEGER" property="subParentId"/>
        </collection>
    </resultMap>
    <select id="getSubCategorys" resultMap="subCategoryDTO" parameterType="INTEGER">
        SELECT p.id as id,p.`name` as `name`,p.`type` as `type`,p.father_id as parentId,
        c.id as subId,c.`name` as subName,c.`type` as subType,c.parent_id as subParentId
        FROM category p
        LEFT JOIN category c
        ON p.id = c.parent_id
        WHERE p.parent_id = ${parentId};
    </select>
</mapper>
```

> **TIPS**  
> 上述创建的package,一定要在项目的启动类`com.liferunner.api.ApiApplication`中修改`@MapperScan(basePackages = {        "com.liferunner.mapper",        "com.liferunner.custom"})`,如果不把我们的`custom` package加上，会造成扫描不到而报错。

在上面的xml中，我们定义了两个DTO对象，分别用来处理二级和三级分类的DTO，实现如下：

```java
@Data
@ToString
public class SecondSubCategoryResponseDTO {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类类型
     1:一级大分类
     2:二级分类
     3:三级小分类
     */
    private Integer type;

    /**
     * 父id
     */
    private Integer parentId;

    List<ThirdSubCategoryResponseDTO> thirdSubCategoryResponseDTOList;
}
---
    
@Data
@ToString
public class ThirdSubCategoryResponseDTO {
    /**
     * 主键
     */
    private Integer subId;

    /**
     * 分类名称
     */
    private String subName;

    /**
     * 分类类型
     1:一级大分类
     2:二级分类
     3:三级小分类
     */
    private Integer subType;

    /**
     * 父id
     */
    private Integer subParentId;
}
```

##### Service实现
编写完自定义mapper之后，我们就可以继续编写service了，在`com.liferunner.service.ICategoryService`中新增一个方法：`getAllSubCategorys(parentId)`.如下：

```java
public interface ICategoryService {
	...
    /**
     * 根据一级分类获取子分类
     *
     * @param parentId 一级分类id
     * @return 子分类list
     */
    List<SecondSubCategoryResponseDTO> getAllSubCategorys(Integer parentId);
}
```

在`com.liferunner.service.impl.CategorySericeImpl`实现上述方法：

```java
@Service
@Slf4j
public class CategorySericeImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryCustomMapper categoryCustomMapper;
	...
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<SecondSubCategoryResponseDTO> getAllSubCategorys(Integer parentId) {
        return this.categoryCustomMapper.getSubCategorys(parentId);
    }
}
```

##### Controller实现

```java
@RestController
@RequestMapping("/index")
@Api(value = "首页信息controller", tags = "首页信息接口API")
@Slf4j
public class IndexController {
    @Autowired
    private ICategoryService categoryService;
	...
    @GetMapping("/subCategorys/{parentId}")
    @ApiOperation(value = "查询子分类", notes = "根据一级分类id查询子分类")
    public JsonResponse findAllSubCategorys(
            @ApiParam(name = "parentId", value = "一级分类id", required = true)
            @PathVariable Integer parentId) {
        log.info("============查询id = {}的子分类==============", parentId);
        val categoryResponseDTOS = this.categoryService.getAllSubCategorys(parentId);
        if (CollectionUtils.isEmpty(categoryResponseDTOS)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============子分类查询result：{}==============", categoryResponseDTOS);
        return JsonResponse.ok(categoryResponseDTOS);
    }
}
```

##### Test API

```json
{
  "status": 200,
  "message": "OK",
  "data": [
    {
      "id": 11,
      "name": "国产",
      "type": 2,
      "parentId": 1,
      "thirdSubCategoryResponseDTOList": [
        {
          "subId": 37,
          "subName": "中华",
          "subType": 3,
          "subParentId": 11
        },
        {
          "subId": 38,
          "subName": "冬虫夏草",
          "subType": 3,
          "subParentId": 11
        },
        {
          "subId": 39,
          "subName": "南京",
          "subType": 3,
          "subParentId": 11
        },
        {
          "subId": 40,
          "subName": "云烟",
          "subType": 3,
          "subParentId": 11
        }
      ]
    },
    {
      "id": 12,
      "name": "外烟",
      "type": 2,
      "parentId": 1,
      "thirdSubCategoryResponseDTOList": [
        {
          "subId": 44,
          "subName": "XXXXX",
          "subType": 3,
          "subParentId": 12
        },
        {
          "subId": 45,
          "subName": "RRRRR",
          "subType": 3,
          "subParentId": 12
        }
      ]
    }
  ],
  "ok": true
}
```

以上我们就已经实现了和`jd`类似的商品分类的功能实现。

## 轮播广告|SlideAD
---

### 需求分析
这个就是`jd`或者`tb`首先的最顶部的广告图片是一样的，每隔1秒自动切换图片。接下来我们分析一下轮播图中都包含哪些信息：
![Slide Images](https://i.loli.net/2019/11/18/ba16sOQ8vWh75Hu.png)

- 图片(img_url)是最基本的
- 图片跳转连接（img_link_url），这个是在我们点击这个图片的时候需要跳转到的页面
- 有的可以直接跳转到商品详情页面
- 有的可以直接跳转到某一分类商品列表页面
- 轮播图的播放顺序（sort）
- ...

### 开发梳理
直接查询出所有的`有效的`轮播图片，并且进行`排序`。

### 编码实现
#### Service 实现
和商品分类实现一样，在`mscx-shop-service`中创建`com.liferunner.service.ISlideAdService`并实现，代码如下：

```java
public interface ISlideAdService {
    /**
     * 查询所有可用广告并排序
     * @param isShow
     * @return
     */
    List<SlideAdResponseDTO> findAll(Integer isShow, String sortRanking);
}
```

```java
@Service
@Slf4j
public class SlideAdServiceImpl implements ISlideAdService {

    // 注入mapper
    private final SlideAdsMapper slideAdsMapper;

    @Autowired
    public SlideAdServiceImpl(SlideAdsMapper slideAdsMapper) {
        this.slideAdsMapper = slideAdsMapper;
    }

    @Override
    public List<SlideAdResponseDTO> findAll(Integer isShow, String sortRanking) {
        Example example = new Example(SlideAds.class);
        //设置排序
        if (StringUtils.isBlank(sortRanking)) {
            example.orderBy("sort").asc();
        } else {
            example.orderBy("sort").desc();
        }
        val conditions = example.createCriteria();
        conditions.andEqualTo("isShow", isShow);
        val slideAdsList = this.slideAdsMapper.selectByExample(example);
        //声明返回对象
        List<SlideAdResponseDTO> slideAdResponseDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(slideAdsList)) {
            //赋值
            SlideAdResponseDTO dto;
            for (SlideAds slideAds : slideAdsList) {
                dto = new SlideAdResponseDTO();
                BeanUtils.copyProperties(slideAds, dto);
                slideAdResponseDTOList.add(dto);
            }
        }
        return slideAdResponseDTOList;
    }
}
```

从上述可以看到，这里我使用的是`构造函数注入SlideAdsMapper`，其余代码单表查询没什么特别的，根据条件查询轮播图，并返回结果，返回的对象是`com.liferunner.dto.SlideAdResponseDTO`列表，代码如下：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "轮播广告返回DTO", description = "轮播广告返回DTO")
public class SlideAdResponseDTO{
    /**
     * 主键
     */
    private String id;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     *  背景颜色
     */
    private String backgroundColor;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商品分类id
     */
    private String catId;

    /**
     * 图片跳转URL
     */
    private String imageLinkUrl;

    /**
     * 轮播图类型 用于判断，可以根据商品id或者分类进行页面跳转，1：商品 2：分类 3：链接url
     */
    private Integer type;

    /**
     * 轮播图展示顺序 轮播图展示顺序，从小到大
     */
    private Integer sort;

    /**
     * 是否展示 是否展示，1：展示    0：不展示
     */
    private Integer isShow;

    /**
     * 创建时间 创建时间
     */
    private Date createTime;

    /**
     * 更新时间 更新
     */
    private Date updateTime;
}
```

#### Controller实现
在`com.liferunner.api.controller.IndexController`中，新添加一个查询轮播图API，代码如下：

```java
	@Autowired
    private ISlideAdService slideAdService;

    @GetMapping("/slideAds")
    @ApiOperation(value = "查询轮播广告", notes = "查询轮播广告接口")
    public JsonResponse findAllSlideList() {
        log.info("============查询所有轮播广告,isShow={},sortRanking={}=============="
                , 1, "desc");
        val slideAdsList = this.slideAdService.findAll(1, "desc");
        if (CollectionUtils.isEmpty(slideAdsList)) {
            log.info("============未查询到任何轮播广告==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============轮播广告查询result：{}=============="
                , slideAdsList);
        return JsonResponse.ok(slideAdsList);
    }
```

#### Test API

```json
{
  "status": 200,
  "message": "OK",
  "data": [
    {
      "id": "slide-100002",
      "imageUrl": "http://www.life-runner.com/2019/11/CpoxxF0ZmH6AeuRrAAEZviPhyQ0768.png",
      "backgroundColor": "#55be59",
      "productId": "",
      "catId": "133",
      "type": 2,
      "sort": 2,
      "isShow": 1,
      "createTime": "2019-10-11T21:33:01.000+0000",
      "updateTime": "2019-10-11T21:33:02.000+0000"
    },
    {
      "id": "slide-100003",
      "imageUrl": "http://www.life-runner.com/2019/11/CpoxxF0ZmHuAPlXvAAFe-H5_-Nw961.png",
      "backgroundColor": "#ff9801",
      "productId": "y200008",
      "catId": "",
      "type": 1,
      "sort": 1,
      "isShow": 1,
      "createTime": "2019-10-11T21:33:01.000+0000",
      "updateTime": "2019-10-11T21:33:02.000+0000"
    }
  ],
  "ok": true
}
```

### 福利讲解
在我们的实现代码中，有心的同学可以看到，我使用了3种不同的Bean注入方式：
- 属性注入

```java
    @Autowired
    private ISlideAdService slideAdService;
```

- 构造函数注入

```java
    // 注入mapper
    private final SlideAdsMapper slideAdsMapper;

    @Autowired
    public SlideAdServiceImpl(SlideAdsMapper slideAdsMapper) {
        this.slideAdsMapper = slideAdsMapper;
    }
```

- Lombok插件注入(本质也是构造器注入，代码会动态生成。)

```java
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements IProductService {
    // RequiredArgsConstructor 构造器注入
    private final ProductCustomMapper productCustomMapper;
    private final ProductsMapper productsMapper;
    ...
}
```

那么，这几种注入都有什么区别呢？首先我们下了解一下Spring的注入是干什么的？

> Spring提出了依赖注入的思想，即依赖类不由程序员实例化，而是通过Spring容器帮我们new指定实例并且将实例注入到需要该对象的类中。  
> 依赖注入的另一种说法是"控制反转"。通俗的理解是：平常我们new一个实例，这个实例的控制权是我们程序员， 而控制反转是指new实例工作不由我们程序员来做而是交给Spring容器来做。

在传统的SpringMVC中，大家使用的都是`XML注入`，比如：

```xml
<!--配置bean,配置后该类由spring管理--> 
<bean name="CategorySericeImpl" class="com.liferunner.service.impl.CategorySericeImpl"> 
<!--注入配置当前类中相应的属性--> 
<property name="categoryMapper" ref="categoryMapper"></property> 
</bean> 
<bean name="categoryMapper" class="com.liferunner.mapper.CategoryMapper"></bean>
```

注入之后，使用`@Autowired`,我们可以很方便的自动从IOC容器中查找属性，并返回。

> **@Autowired的原理**  
> 在启动spring IoC时，容器自动装载了一个`AutowiredAnnotationBeanPostProcessor`后置处理器，当容器扫描到@Autowied、@Resource或@Inject时，就会在IoC容器自动查找需要的bean，并装配给该对象的属性。  
>  **注意事项：**  
> 在使用@Autowired时，首先在容器中查询对应类型的bean
> 		如果查询结果刚好为一个，就将该bean装配给@Autowired指定的数据  
>		如果查询的结果不止一个，那么@Autowired会根据名称来查找。  
>		如果查询的结果为空，那么会抛出异常。解决方法时，使用required=false  
> [source传送门](https://www.cnblogs.com/caoyc/p/5626365.html)

上述三种注解方式，其实本质上还是注入的2种:`set属性注入` & `构造器注入`，使用方式都可以，根据个人喜好来用，本人喜欢使用`lombok插件注入`是因为，它将代码整合在一起，更加符合我们Spring自动注入的规范。 

## 源码下载
---

[Github 传送门](https://github.com/Isaac-Zhang/expensive-shop)  
[Gitee 传送门](https://gitee.com/IsaacZhang/expensive-shop)

## 下节预告
---

下一节我们将继续开发我们电商的核心部分-商品列表和详情展示，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！