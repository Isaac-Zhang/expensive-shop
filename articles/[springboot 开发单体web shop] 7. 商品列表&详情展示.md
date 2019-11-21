# 上文回顾

[上节](https://segmentfault.com/a/1190000021047932) 我们实现了仿`jd`的轮播广告以及商品分类的功能，并且讲解了不同的注入方式，本节我们将继续实现我们的电商主业务，商品信息的展示。

# 需求分析

首先，在我们开始本节编码之前，我们先来分析一下都有哪些地方会对商品进行展示，打开`jd`首页，鼠标下拉可以看到如下：  
![首页商品列表示例](https://i.loli.net/2019/11/19/Y2tQj8ukEDW3ZIB.png)

可以看到，在大类型下查询了部分商品在首页进行展示（可以是最新的，也可以是网站推荐等等），然后点击任何一个分类，可以看到如下：  
![分类商品列表示例](https://i.loli.net/2019/11/19/O9WRHNpvQroUMnV.png)

我们一般进到电商网站之后，最常用的一个功能就是搜索，[搜索钢琴]([https://search.jd.com/Search?keyword=%E7%94%B5%E9%92%A2%E7%90%B4&enc=utf-8&spm=a.0.0&wq=&pvid=2162c63a5cb942d0aa73b921fb00dca9](https://search.jd.com/Search?keyword=电钢琴&enc=utf-8&spm=a.0.0&wq=&pvid=2162c63a5cb942d0aa73b921fb00dca9)) 结果如下：   
![搜索查询结果示例](https://i.loli.net/2019/11/19/RGeoUry9f8Yxqin.png)  
选择任意一个商品点击，都可以进入到详情页面，这个是单个商品的信息展示。
综上，我们可以知道，要实现一个电商平台的商品展示，最基本的包含：

- 首页推荐/最新上架商品
- 分类查询商品
- 关键词搜索商品
- 商品详情展示
- ...

接下来，我们就可以开始商品相关的业务开发了。

# 首页商品列表|IndexProductList

## 开发梳理
我们首先来实现在首页展示的推荐商品列表，来看一下都需要展示哪些信息，以及如何进行展示。

- 商品主键(product_id)
- 展示图片（image_url）
- 商品名称（product_name）
- 商品价格(product_price)
- 分类说明(description)
- 分类名称(category_name)
- 分类主键（category_id）
- 其他...

## 编码实现

### 根据一级分类查询

遵循开发顺序，自下而上，如果基础mapper解决不了，那么优先编写SQL mapper，因为我们需要在同一张表中根据`parent_id`递归的实现数据查询，当然我们这里使用的是`表链接`的方式实现。因此，`common mapper`无法满足我们的需求，需要自定义mapper实现。

#### Custom Mapper实现

和[上节](https://segmentfault.com/a/1190000021047932)根据一级分类查询子分类一样，在项目`mscx-shop-mapper`中添加一个自定义实现接口`com.liferunner.custom.ProductCustomMapper`，然后在`resources\mapper\custom`路径下同步创建xml文件`mapper/custom/ProductCustomMapper.xml`，此时，因为我们在上节中已经配置了当前文件夹可以被容器扫描到，所以我们添加的新的mapper就会在启动时被扫描加载，代码如下：

```java
/**
 * ProductCustomMapper for : 自定义商品Mapper
 */
public interface ProductCustomMapper {

    /***
     * 根据一级分类查询商品
     *
     * @param paramMap 传递一级分类（map传递多参数）
     * @return java.util.List<com.liferunner.dto.IndexProductDTO>
     */
    List<IndexProductDTO> getIndexProductDtoList(@Param("paramMap") Map<String, Integer> paramMap);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.liferunner.custom.ProductCustomMapper">
    <resultMap id="IndexProductDTO" type="com.liferunner.dto.IndexProductDTO">
        <id column="rootCategoryId" property="rootCategoryId"/>
        <result column="rootCategoryName" property="rootCategoryName"/>
        <result column="slogan" property="slogan"/>
        <result column="categoryImage" property="categoryImage"/>
        <result column="bgColor" property="bgColor"/>
        <collection property="productItemList" ofType="com.liferunner.dto.IndexProductItemDTO">
            <id column="productId" property="productId"/>
            <result column="productName" property="productName"/>
            <result column="productMainImageUrl" property="productMainImageUrl"/>
            <result column="productCreateTime" property="productCreateTime"/>
        </collection>
    </resultMap>
    <select id="getIndexProductDtoList" resultMap="IndexProductDTO" parameterType="Map">
        SELECT
        c.id as rootCategoryId,
        c.name as rootCategoryName,
        c.slogan as slogan,
        c.category_image as categoryImage,
        c.bg_color as bgColor,
        p.id as productId,
        p.product_name as productName,
        pi.url as productMainImageUrl,
        p.created_time as productCreateTime
        FROM category c
        LEFT JOIN products p
        ON c.id = p.root_category_id
        LEFT JOIN products_img pi
        ON p.id = pi.product_id
        WHERE c.type = 1
        AND p.root_category_id = #{paramMap.rootCategoryId}
        AND pi.is_main = 1
        LIMIT 0,10;
    </select>
</mapper>
```

#### Service实现

在`service`project 创建`com.liferunner.service.IProductService接口`以及其实现类`com.liferunner.service.impl.ProductServiceImpl`,添加查询方法如下：

```java
public interface IProductService {

    /**
     * 根据一级分类id获取首页推荐的商品list
     *
     * @param rootCategoryId 一级分类id
     * @return 商品list
     */
    List<IndexProductDTO> getIndexProductDtoList(Integer rootCategoryId);
	...
}

---
    
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductServiceImpl implements IProductService {

    // RequiredArgsConstructor 构造器注入
    private final ProductCustomMapper productCustomMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<IndexProductDTO> getIndexProductDtoList(Integer rootCategoryId) {
        log.info("====== ProductServiceImpl#getIndexProductDtoList(rootCategoryId) : {}=======", rootCategoryId);
        Map<String, Integer> map = new HashMap<>();
        map.put("rootCategoryId", rootCategoryId);
        val indexProductDtoList = this.productCustomMapper.getIndexProductDtoList(map);
        if (CollectionUtils.isEmpty(indexProductDtoList)) {
            log.warn("ProductServiceImpl#getIndexProductDtoList未查询到任何商品信息");
        }
        log.info("查询结果：{}", indexProductDtoList);
        return indexProductDtoList;
    }
}
```

#### Controller实现
接着，在`com.liferunner.api.controller.IndexController`中实现对外暴露的查询接口：

```java
@RestController
@RequestMapping("/index")
@Api(value = "首页信息controller", tags = "首页信息接口API")
@Slf4j
public class IndexController {
   	...
    @Autowired
    private IProductService productService;

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
    ...
}
```

#### Test API
编写完成之后，我们需要对我们的代码进行测试验证，还是通过使用`RestService`插件来实现，当然，大家也可以通过Postman来测试，结果如下：  
![根据一级分类查询商品列表](https://i.loli.net/2019/11/20/Zm23S6CvlUdQfze.png)

# 商品列表|ProductList

如开文之初我们看到的京东商品列表一样，我们先分析一下在商品列表页面都需要哪些元素信息？

## 开发梳理

商品列表的展示按照我们之前的分析，总共分为2大类:

- 选择商品分类之后，展示当前分类下所有商品
- 输入搜索关键词后，展示当前搜索到相关的所有商品

在这两类中展示的商品列表数据，除了数据来源不同以外，其他元素基本都保持一致，那么我们是否可以使用统一的接口来根据参数实现隔离呢？ 理论上不存在问题，完全可以通过传参判断的方式进行数据回传，但是，在我们实现一些可预见的功能需求时，一定要给自己的开发预留后路，也就是我们常说的`可拓展性`，基于此，我们会分开实现各自的接口，以便于后期的扩展。
接着来分析在列表页中我们需要展示的元素，首先因为需要分上述两种情况，因此我们需要在我们API设计的时候分别处理，针对于  
1.分类的商品列表展示，需要传入的参数有：

- 分类id
- 排序（在电商列表我们常见的几种排序（销量，价格等等））
- 分页相关（因为我们不可能把数据库中所有的商品都取出来）
  - PageNumber（当前第几页）
  - PageSize（每页显示多少条数据）

2.关键词查询商品列表，需要传入的参数有：

- 关键词
- 排序（在电商列表我们常见的几种排序（销量，价格等等））
- 分页相关（因为我们不可能把数据库中所有的商品都取出来）
  - PageNumber（当前第几页）
  - PageSize（每页显示多少条数据）

需要在页面展示的信息有：

- 商品id(用于跳转商品详情使用)
- 商品名称
- 商品价格
- 商品销量
- 商品图片
- 商品优惠
- ...

## 编码实现

根据上面我们的分析，接下来开始我们的编码：

### 根据商品分类查询

根据我们的分析，肯定不会在一张表中把所有数据获取全，因此我们需要进行多表联查，故我们需要在自定义mapper中实现我们的功能查询.

#### ResponseDTO 实现

根据我们前面分析的前端需要展示的信息，我们来定义一个用于展示这些信息的对象`com.liferunner.dto.SearchProductDTO`，代码如下：  

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProductDTO {
    private String productId;
    private String productName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer priceDiscount;
    //商品优惠，我们直接计算之后返回优惠后价格
}
```

#### Custom Mapper 实现

在`com.liferunner.custom.ProductCustomMapper.java`中新增一个方法接口：  

```java
    List<SearchProductDTO> searchProductListByCategoryId(@Param("paramMap") Map<String, Object> paramMap);
```

同时，在`mapper/custom/ProductCustomMapper.xml`中实现我们的查询方法：  

```xml
<select id="searchProductListByCategoryId" resultType="com.liferunner.dto.SearchProductDTO" parameterType="Map">
        SELECT
        p.id as productId,
        p.product_name as productName,
        p.sell_counts as sellCounts,
        pi.url as imgUrl,
        tp.priceDiscount
        FROM products p
        LEFT JOIN products_img pi
        ON p.id = pi.product_id
        LEFT JOIN
        (
        SELECT product_id, MIN(price_discount) as priceDiscount
        FROM products_spec
        GROUP BY product_id
        ) tp
        ON tp.product_id = p.id
        WHERE pi.is_main = 1
        AND p.category_id = #{paramMap.categoryId}
        ORDER BY
        <choose>
            <when test="paramMap.sortby != null and paramMap.sortby == 'sell'">
                p.sell_counts DESC
            </when>
            <when test="paramMap.sortby != null and paramMap.sortby == 'price'">
                tp.priceDiscount ASC
            </when>
            <otherwise>
                p.created_time DESC
            </otherwise>
        </choose>
    </select>
```

主要来说明一下这里的`<choose>`模块，以及为什么不使用`if`标签。  
在有的时候，我们并不希望所有的条件都同时生效，而只是想从多个选项中选择一个，但是在使用`IF`标签时，只要`test`中的表达式为 `true`，就会执行`IF` 标签中的条件。MyBatis 提供了 `choose` 元素。`IF`标签是`与(and)`的关系，而 choose 是`或(or)`的关系。  
它的选择是按照顺序自上而下，一旦有任何一个满足条件，则选择退出。

#### Service 实现

然后在service`com.liferunner.service.IProductService`中添加方法接口：  

```java
    /**
     * 根据商品分类查询商品列表
     *
     * @param categoryId 分类id
     * @param sortby     排序方式
     * @param pageNumber 当前页码
     * @param pageSize   每页展示多少条数据
     * @return 通用分页结果视图
     */
    CommonPagedResult searchProductList(Integer categoryId, String sortby, Integer pageNumber, Integer pageSize);
```

在实现类`com.liferunner.service.impl.ProductServiceImpl`中，实现上述方法：  

```java
    // 方法重载
    @Override
    public CommonPagedResult searchProductList(Integer categoryId, String sortby, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("categoryId", categoryId);
        paramMap.put("sortby", sortby);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val searchProductDTOS = this.productCustomMapper.searchProductListByCategoryId(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(searchProductDTOS)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }
```

在这里，我们使用到了一个`mybatis-pagehelper`插件，会在下面的福利讲解中分解。  

#### Controller 实现

继续在`com.liferunner.api.controller.ProductController`中添加对外暴露的接口API：

```java
@GetMapping("/searchByCategoryId")
    @ApiOperation(value = "查询商品信息列表", notes = "根据商品分类查询商品列表")
    public JsonResponse searchProductListByCategoryId(
        @ApiParam(name = "categoryId", value = "商品分类id", required = true, example = "0")
        @RequestParam Integer categoryId,
        @ApiParam(name = "sortby", value = "排序方式", required = false)
        @RequestParam String sortby,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (null == categoryId || categoryId == 0) {
            return JsonResponse.errorMsg("分类id错误！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============根据分类:{} 搜索列表==============", categoryId);

        val searchResult = this.productService.searchProductList(categoryId, sortby, pageNumber, pageSize);
        return JsonResponse.ok(searchResult);
    }
```

因为我们的请求中，只会要求商品分类id是必填项，其余的调用方都可以不提供，但是如果不提供的话，我们系统就需要给定一些默认的参数来保证我们的系统正常稳定的运行，因此，我定义了`com.liferunner.api.controller.BaseController`,用于存储一些公共的配置信息。

```java
/**
 * BaseController for : controller 基类
 */
@Controller
public class BaseController {
    /**
     * 默认展示第1页
     */
    public final Integer DEFAULT_PAGE_NUMBER = 1;
    /**
     * 默认每页展示10条数据
     */
    public final Integer DEFAULT_PAGE_SIZE = 10;
}
```

#### Test API

测试的参数分别是：categoryId : 51 ，sortby : price，pageNumber : 1，pageSize : 5  

![根据分类id查询](https://i.loli.net/2019/11/21/KMzdrx4oc9mwNQB.png)  

可以看到，我们查询到7条数据，总页数`totalPage`为2，并且根据价格从小到大进行了排序，证明我们的编码是正确的。接下来，通过相同的代码逻辑，我们继续实现根据搜索关键词进行查询。

### 根据关键词查询

#### Response DTO 实现

使用上面实现的`com.liferunner.dto.SearchProductDTO`.

#### Custom Mapper 实现

在`com.liferunner.custom.ProductCustomMapper`中新增方法：

```java
List<SearchProductDTO> searchProductList(@Param("paramMap") Map<String, Object> paramMap);
```

在`mapper/custom/ProductCustomMapper.xml`中添加查询SQL：

```xml
<select id="searchProductList" resultType="com.liferunner.dto.SearchProductDTO" parameterType="Map">
        SELECT
        p.id as productId,
        p.product_name as productName,
        p.sell_counts as sellCounts,
        pi.url as imgUrl,
        tp.priceDiscount
        FROM products p
        LEFT JOIN products_img pi
        ON p.id = pi.product_id
        LEFT JOIN
        (
        SELECT product_id, MIN(price_discount) as priceDiscount
        FROM products_spec
        GROUP BY product_id
        ) tp
        ON tp.product_id = p.id
        WHERE pi.is_main = 1
        <if test="paramMap.keyword != null and paramMap.keyword != ''">
            AND p.item_name LIKE "%${paramMap.keyword}%"
        </if>
        ORDER BY
        <choose>
            <when test="paramMap.sortby != null and paramMap.sortby == 'sell'">
                p.sell_counts DESC
            </when>
            <when test="paramMap.sortby != null and paramMap.sortby == 'price'">
                tp.priceDiscount ASC
            </when>
            <otherwise>
                p.created_time DESC
            </otherwise>
        </choose>
    </select>
```

#### Service 实现

在`com.liferunner.service.IProductService`中新增查询接口：

```java
    /**
     * 查询商品列表
     *
     * @param keyword    查询关键词
     * @param sortby     排序方式
     * @param pageNumber 当前页码
     * @param pageSize   每页展示多少条数据
     * @return 通用分页结果视图
     */
    CommonPagedResult searchProductList(String keyword, String sortby, Integer pageNumber, Integer pageSize);
```

在`com.liferunner.service.impl.ProductServiceImpl`实现上述接口方法：

```java
    @Override
    public CommonPagedResult searchProductList(String keyword, String sortby, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("sortby", sortby);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val searchProductDTOS = this.productCustomMapper.searchProductList(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(searchProductDTOS)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }
```

上述方法和之前`searchProductList(Integer categoryId, String sortby, Integer pageNumber, Integer pageSize) `唯一的区别就是它是肯定搜索关键词来进行数据查询，使用重载的目的是为了我们后续不同类型的业务扩展而考虑的。

#### Controller 实现

在`com.liferunner.api.controller.ProductController`中添加关键词搜索API：

```java
    @GetMapping("/search")
    @ApiOperation(value = "查询商品信息列表", notes = "查询商品信息列表")
    public JsonResponse searchProductList(
        @ApiParam(name = "keyword", value = "搜索关键词", required = true)
        @RequestParam String keyword,
        @ApiParam(name = "sortby", value = "排序方式", required = false)
        @RequestParam String sortby,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (StringUtils.isBlank(keyword)) {
            return JsonResponse.errorMsg("搜索关键词不能为空！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============根据关键词:{} 搜索列表==============", keyword);

        val searchResult = this.productService.searchProductList(keyword, sortby, pageNumber, pageSize);
        return JsonResponse.ok(searchResult);
    }
```

#### Test API

测试参数：keyword : 西凤，sortby : sell，pageNumber : 1，pageSize : 10  
![测试关键词结果](https://i.loli.net/2019/11/21/p5fa8QnUHySFico.png)  
根据销量排序正常，查询关键词正常，总条数32，每页10条，总共3页正常。

# 福利讲解

在本节编码实现中，我们使用到了一个通用的mybatis分页插件`mybatis-pagehelper`,接下来，我们来了解一下这个插件的基本情况。

## `mybatis-pagehelper`

如果各位小伙伴使用过：[MyBatis 分页插件 PageHelper](https://pagehelper.github.io/), 那么对于这个就很容易理解了，它其实就是基于[Executor 拦截器](https://pagehelper.github.io/docs/interceptor/)来实现的，当拦截到原始SQL之后，对SQL进行一次改造处理。  
我们来看看我们自己代码中的实现，根据springboot编码三部曲：

1.添加依赖

```xml
        <!-- 引入mybatis-pagehelper 插件-->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.12</version>
        </dependency>
```

有同学就要问了，为什么引入的这个依赖和我原来使用的不同？以前使用的是：

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.1.10</version>
</dependency>
```

答案就在这里：[依赖传送门](https://github.com/pagehelper)  
![spring-boot-pagehelper](https://i.loli.net/2019/11/21/xn9if3tFrdqBkeM.png)  
我们使用的是springboot进行的项目开发，既然使用的是springboot,那我们完全可以用到它的`自动装配`特性,作者帮我们实现了这么一个[自动装配的jar](https://github.com/pagehelper/pagehelper-spring-boot),我们只需要参考示例来编写就ok了。

2.改配置

```yaml
# mybatis 分页组件配置
pagehelper:
  helperDialect: mysql #插件支持12种数据库，选择类型
  supportMethodsArguments: true
```

3.改代码

如下示例代码：

```java
    @Override
    public CommonPagedResult searchProductList(String keyword, String sortby, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("sortby", sortby);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val searchProductDTOS = this.productCustomMapper.searchProductList(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(searchProductDTOS)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }
```

在我们查询数据库之前，我们引入了一句`PageHelper.startPage(pageNumber, pageSize);`,告诉mybatis我们要对查询进行分页处理，这个时候插件会启动一个拦截器`com.github.pagehelper.PageInterceptor`,针对所有的`query`进行拦截，添加自定义参数和添加查询数据总数。（后续我们会打印sql来证明。）

当查询到结果之后，我们需要将我们查询到的结果通知给插件，也就是`PageInfo<?> pageInfo = new PageInfo<>(searchProductDTOS);`（`com.github.pagehelper.PageInfo`是对插件针对分页做的一个属性包装，具体可以查看[属性传送门](http://bbs.csdn.net/topics/360010907)）。

至此，我们的插件使用就已经结束了。但是为什么我们在后面又封装了一个对象来对外进行返回，而不是使用查询到的`PageInfo`呢？这是因为我们实际开发过程中，为了数据结构的一致性做的一次结构封装，你也可不实现该步骤，都是对结果没有任何影响的。

## SQL打印对比

```shell
2019-11-21 12:04:21 INFO  ProductController:134 - ============根据关键词:西凤 搜索列表==============
Creating a new SqlSession
SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@4ff449ba] was not registered for synchronization because synchronization is not active
JDBC Connection [HikariProxyConnection@1980420239 wrapping com.mysql.cj.jdbc.ConnectionImpl@563b22b1] will not be managed by Spring
==>  Preparing: SELECT count(0) FROM products p LEFT JOIN products_img pi ON p.id = pi.product_id LEFT JOIN (SELECT product_id, MIN(price_discount) AS priceDiscount FROM products_spec GROUP BY product_id) tp ON tp.product_id = p.id WHERE pi.is_main = 1 AND p.product_name LIKE "%西凤%" 
==> Parameters: 
<==    Columns: count(0)
<==        Row: 32
<==      Total: 1
==>  Preparing: SELECT p.id as productId, p.product_name as productName, p.sell_counts as sellCounts, pi.url as imgUrl, tp.priceDiscount FROM product p LEFT JOIN products_img pi ON p.id = pi.product_id LEFT JOIN ( SELECT product_id, MIN(price_discount) as priceDiscount FROM products_spec GROUP BY product_id ) tp ON tp.product_id = p.id WHERE pi.is_main = 1 AND p.product_name LIKE "%西凤%" ORDER BY p.sell_counts DESC LIMIT ? 
==> Parameters: 10(Integer)
```

我们可以看到，我们的SQL中多了一个`SELECT count(0)`，第二条SQL多了一个`LIMIT`参数，在代码中，我们很明确的知道，我们并没有显示的去搜索总数和查询条数，可以确定它就是插件帮我们实现的。

# 源码下载

[Github 传送门](https://github.com/Isaac-Zhang/expensive-shop)  
[Gitee 传送门](https://gitee.com/IsaacZhang/expensive-shop)

# 下节预告

下一节我们将继续开发商品详情展示以及商品评价业务，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！