# 上文回顾

[上节](https://segmentfault.com/a/1190000021082968) 我们实现了根据搜索关键词查询商品列表和根据商品分类查询，并且使用到了`mybatis-pagehelper`插件，讲解了如何使用插件来帮助我们快速实现分页数据查询。本文我们将继续开发商品详情页面和商品留言功能的开发。

# 需求分析

关于商品详情页，和往常一样，我们先来看一看`jd`的示例：  
![商品展示详情](https://i.loli.net/2019/11/23/hl7Cpi5I9qPKdxy.png)  
![商品介绍](https://i.loli.net/2019/11/23/cGekB9TDpUuF8A5.png)  
从上面2张图，我们可以看出来，大体上需要展示给用户的信息。比如：商品图片，名称，价格，等等。在第二张图中，我们还可以看到有一个`商品评价页签`,这些都是我们本节要实现的内容。

# 商品详情

## 开发梳理
我们根据上图（权当是需求文档，*很多需求文档写的比这个可能还差劲很多...*）分析一下，我们的开发大致都要关注哪些`points`:

- 商品标题
- 商品图片集合
- 商品价格（原价以及优惠价）
- 配送地址（我们的实现不在此，我们后续直接实现在下单逻辑中）
- 商品规格
- 商品分类
- 商品销量
- 商品详情
- 商品参数（生产场地，日期等等）
- ...

根据我们梳理出来的信息，接下来开始编码就会很简单了，大家可以根据之前课程讲解的，先自行实现一波，请开始你们的表演～

## 编码实现
### DTO实现

因为我们在实际的数据传输过程中，不可能直接把我们的数据库`entity`之间暴露到前端，而且我们商品相关的数据是存储在不同的数据表中，我们必须要封装一个`ResponseDTO`来对数据进行传递。

- `ProductDetailResponseDTO`包含了商品主表信息，以及图片列表、商品规格(不同SKU)以及商品具体参数（产地，生产日期等信息）

```java
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDTO {
    private Products products;
    private List<ProductsImg> productsImgList;
    private List<ProductsSpec> productsSpecList;
    private ProductsParam productsParam;
}
```

### Custom Mapper实现

根据我们之前表的设计，这里使用生成的通用mapper就可以满足我们的需求。

### Service实现

从我们封装的要传递到前端的`ProductDetailResponseDTO`就可以看出，我们可以根据`商品id`分别查询出商品的相关信息，在`controller`进行数据封装就可以了，来实现我们的查询接口。

- 查询商品主表信息（名称，内容等）

  在`com.liferunner.service.IProductService`中添加接口方法：

  ```java
      /**
       * 根据商品id查询商品
       *
       * @param pid 商品id
       * @return 商品主信息
       */
      Products findProductByPid(String pid);
  ```

  接着，在`com.liferunner.service.impl.ProductServiceImpl`中添加实现方法：

  ```java
      @Override
      @Transactional(propagation = Propagation.SUPPORTS)
      public Products findProductByPid(String pid) {
          return this.productsMapper.selectByPrimaryKey(pid);
      }
  ```

  直接使用通用mapper根据主键查询就可以了。
  > 同上，我们依次来实现图片、规格、以及商品参数相关的编码工作

- 查询商品图片信息列表

  ```java
      /**
       * 根据商品id查询商品规格
       *
       * @param pid 商品id
       * @return 规格list
       */
      List<ProductsSpec> getProductSpecsByPid(String pid);
  
  ----------------------------------------------------------------
      
      @Override
      public List<ProductsSpec> getProductSpecsByPid(String pid) {
          Example example = new Example(ProductsSpec.class);
          val condition = example.createCriteria();
          condition.andEqualTo("productId", pid);
          return this.productsSpecMapper.selectByExample(example);
      }
  ```

- 查询商品规格列表

  ```java
      /**
       * 根据商品id查询商品规格
       *
       * @param pid 商品id
       * @return 规格list
       */
      List<ProductsSpec> getProductSpecsByPid(String pid);
  
  ------------------------------------------------------------------
      
      @Override
      public List<ProductsSpec> getProductSpecsByPid(String pid) {
          Example example = new Example(ProductsSpec.class);
          val condition = example.createCriteria();
          condition.andEqualTo("productId", pid);
          return this.productsSpecMapper.selectByExample(example);
      }
  ```

- 查询商品参数信息

  ```java
      /**
       * 根据商品id查询商品参数
       *
       * @param pid 商品id
       * @return 参数
       */
      ProductsParam findProductParamByPid(String pid);
  
  ------------------------------------------------------------------
      
      @Override
      public ProductsParam findProductParamByPid(String pid) {
          Example example = new Example(ProductsParam.class);
          val condition = example.createCriteria();
          condition.andEqualTo("productId", pid);
          return this.productsParamMapper.selectOneByExample(example);
      }
  ```

### Controller实现

在上面将我们需要的信息查询实现之后，然后我们需要在controller对数据进行包装，之后再返回到前端，供用户来进行查看，在`com.liferunner.api.controller.ProductController`中添加对外接口`/detail/{pid}`,实现如下：

```java
	@GetMapping("/detail/{pid}")
    @ApiOperation(value = "根据商品id查询详情", notes = "根据商品id查询详情")
    public JsonResponse findProductDetailByPid(
        @ApiParam(name = "pid", value = "商品id", required = true)
        @PathVariable String pid) {
        if (StringUtils.isBlank(pid)) {
            return JsonResponse.errorMsg("商品id不能为空！");
        }
        val product = this.productService.findProductByPid(pid);
        val productImgList = this.productService.getProductImgsByPid(pid);
        val productSpecList = this.productService.getProductSpecsByPid(pid);
        val productParam = this.productService.findProductParamByPid(pid);
        val productDetailResponseDTO = ProductDetailResponseDTO
            .builder()
            .products(product)
            .productsImgList(productImgList)
            .productsSpecList(productSpecList)
            .productsParam(productParam)
            .build();
        log.info("============查询到商品详情:{}==============", productDetailResponseDTO);

        return JsonResponse.ok(productDetailResponseDTO);
    }
```

从上述代码中可以看到，我们分别查询了商品、图片、规格以及参数信息，使用`ProductDetailResponseDTO.builder().build()`封装成返回到前端的对象。

### Test API

按照惯例，写完代码我们需要进行测试。

```java
{
  "status": 200,
  "message": "OK",
  "data": {
    "products": {
      "id": "smoke-100021",
      "productName": "(奔跑的人生) - 中华",
      "catId": 37,
      "rootCatId": 1,
      "sellCounts": 1003,
      "onOffStatus": 1,
      "createdTime": "2019-09-09T06:45:34.000+0000",
      "updatedTime": "2019-09-09T06:45:38.000+0000",
      "content": "吸烟有害健康“
    },
    "productsImgList": [
      {
        "id": "1",
        "productId": "smoke-100021",
        "url": "http://www.life-runner.com/product/smoke/img1.png",
        "sort": 0,
        "isMain": 1,
        "createdTime": "2019-07-01T06:46:55.000+0000",
        "updatedTime": "2019-07-01T06:47:02.000+0000"
      },
      {
        "id": "2",
        "productId": "smoke-100021",
        "url": "http://www.life-runner.com/product/smoke/img2.png",
        "sort": 1,
        "isMain": 0,
        "createdTime": "2019-07-01T06:46:55.000+0000",
        "updatedTime": "2019-07-01T06:47:02.000+0000"
      },
      {
        "id": "3",
        "productId": "smoke-100021",
        "url": "http://www.life-runner.com/product/smoke/img3.png",
        "sort": 2,
        "isMain": 0,
        "createdTime": "2019-07-01T06:46:55.000+0000",
        "updatedTime": "2019-07-01T06:47:02.000+0000"
      }
    ],
    "productsSpecList": [
      {
        "id": "1",
        "productId": "smoke-100021",
        "name": "中华",
        "stock": 2276,
        "discounts": 1.00,
        "priceDiscount": 7000,
        "priceNormal": 7000,
        "createdTime": "2019-07-01T06:54:20.000+0000",
        "updatedTime": "2019-07-01T06:54:28.000+0000"
      },
    ],
    "productsParam": {
      "id": "1",
      "productId": "smoke-100021",
      "producPlace": "中国",
      "footPeriod": "760天",
      "brand": "中华",
      "factoryName": "中华",
      "factoryAddress": "陕西",
      "packagingMethod": "盒装",
      "weight": "100g",
      "storageMethod": "常温",
      "eatMethod": "",
      "createdTime": "2019-05-01T09:38:30.000+0000",
      "updatedTime": "2019-05-01T09:38:34.000+0000"
    }
  },
  "ok": true
}
```

# 商品评价

在文章一开始我们就看过`jd`详情页面，有一个详情页签，我们来看一下：
![商品评价示例](https://i.loli.net/2019/11/23/koG7vmThiKrs5qn.png)  
它这个实现比较复杂，我们只实现相对重要的几个就可以了。

## 开发梳理

针对上图中红色方框圈住的内容，分别有：

- 评价总数
- 好评度（根据好评总数，中评总数，差评总数计算得出）
- 评价等级
- 以及用户信息加密展示
- 评价内容
- ...

我们来实现上述分析的相对必要的一些内容。

## 编码实现

### 查询评价

根据我们需要的信息，我们需要从用户表、商品表以及评价表中来联合查询数据，很明显单表通用mapper无法实现，因此我们先来实现自定义查询mapper，当然数据的传输对象是我们需要先来定义的。

#### Response DTO实现

创建`com.liferunner.dto.ProductCommentDTO`.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCommentDTO {
    //评价等级
    private Integer commentLevel;
    //规格名称
    private String specName;
    //评价内容
    private String content;
    //评价时间
    private Date createdTime;
    //用户头像
    private String userFace;
    //用户昵称
    private String nickname;
}
```

#### Custom Mapper实现

在`com.liferunner.custom.ProductCustomMapper`中添加查询接口方法：

```java
    /***
     * 根据商品id 和 评价等级查询评价信息
     * <code>
     *         Map<String, Object> paramMap = new HashMap<>();
     *         paramMap.put("productId", pid);
     *         paramMap.put("commentLevel", level);
     *</code>
     * @param paramMap
     * @return java.util.List<com.liferunner.dto.ProductCommentDTO>
     * @throws
     */
    List<ProductCommentDTO> getProductCommentList(@Param("paramMap") Map<String, Object> paramMap);
```

在`mapper/custom/ProductCustomMapper.xml`中实现该接口方法的SQL：

```xml
    <select id="getProductCommentList" resultType="com.liferunner.dto.ProductCommentDTO" parameterType="Map">
        SELECT
        pc.comment_level as commentLevel,
        pc.spec_name as specName,
        pc.content as content,
        pc.created_time as createdTime,
        u.face as userFace,
        u.nickname as nickname
        FROM items_comments pc
        LEFT JOIN users u
        ON pc.user_id = u.id
        WHERE pc.item_id = #{paramMap.productId}
        <if test="paramMap.commentLevel != null and paramMap.commentLevel != ''">
            AND pc.comment_level = #{paramMap.commentLevel}
        </if>
    </select>
```

如果没有传递评价级别的话，默认查询全部评价信息。

#### Service 实现

在`com.liferunner.service.IProductService`中添加查询接口方法：

```java
    /**
     * 查询商品评价
     *
     * @param pid        商品id
     * @param level      评价级别
     * @param pageNumber 当前页码
     * @param pageSize   每页展示多少条数据
     * @return 通用分页结果视图
     */
    CommonPagedResult getProductComments(String pid, Integer level, Integer pageNumber, Integer pageSize);
```

在`com.liferunner.service.impl.ProductServiceImpl`实现该方法：

```java
    @Override
    public CommonPagedResult getProductComments(String pid, Integer level, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("productId", pid);
        paramMap.put("commentLevel", level);
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);
        val productCommentList = this.productCustomMapper.getProductCommentList(paramMap);
        for (ProductCommentDTO item : productCommentList) {
            item.setNickname(SecurityTools.HiddenPartString4SecurityDisplay(item.getNickname()));
        }
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(productCommentList);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
                .pageNumber(pageNumber)
                .rows(productCommentList)
                .totalPage(pageInfo.getPages())
                .records(pageInfo.getTotal())
                .build();
        return commonPagedResult;
    }
```

> 因为评价过多会使用到分页，这里使用通用分页返回结果，关于分页，可查看[学习分页传送门](https://segmentfault.com/a/1190000021082968#item-5-5)。

#### Controller实现

在`com.liferunner.api.controller.ProductController`中添加对外查询接口：

```java
	@GetMapping("/comments")
    @ApiOperation(value = "查询商品评价", notes = "根据商品id查询商品评价")
    public JsonResponse getProductComment(
        @ApiParam(name = "pid", value = "商品id", required = true)
        @RequestParam String pid,
        @ApiParam(name = "level", value = "评价级别", required = false, example = "0")
        @RequestParam Integer level,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (StringUtils.isBlank(pid)) {
            return JsonResponse.errorMsg("商品id不能为空！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============查询商品评价:{}==============", pid);

        val productComments = this.productService.getProductComments(pid, level, pageNumber, pageSize);
        return JsonResponse.ok(productComments);
    }
```

**FBI WARNING**: 
> @ApiParam(name = "level", value = "评价级别", required = false, example = "0")
> @RequestParam Integer level
> 关于ApiParam参数，如果接收参数为非字符串类型，一定要定义example为对应类型的示例值，否则Swagger在访问过程中会报example转换错误，因为example缺省为""空字符串，会转换失败。例如我们删除掉`level`这个字段中的example=”0“，如下为错误信息（但是并不影响程序使用。）

```shell
2019-11-23 15:51:45 WARN  AbstractSerializableParameter:421 - Illegal DefaultValue null for parameter type integer
java.lang.NumberFormatException: For input string: ""
	at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
	at java.lang.Long.parseLong(Long.java:601)
	at java.lang.Long.valueOf(Long.java:803)
	at io.swagger.models.parameters.AbstractSerializableParameter.getExample(AbstractSerializableParameter.java:412)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:688)
	at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:721)
	at com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:166)
	at com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:119)
```

#### Test API

![查询评价结果](https://i.loli.net/2019/11/23/tgOAG4b9Fw6BLIx.png)

# 福利讲解
## 添加Propagation.SUPPORTS和不加的区别

有心的小伙伴肯定又注意到了，在Service中处理查询时，我一部分使用了`@Transactional(propagation = Propagation.SUPPORTS)`,一部分查询又没有添加事务，那么这两种方式有什么不一样呢？接下来，我们来揭开神秘的面纱。

- Propagation.SUPPORTS

  ```java
  	/**
  	 * Support a current transaction, execute non-transactionally if none exists.
  	 * Analogous to EJB transaction attribute of the same name.
  	 * <p>Note: For transaction managers with transaction synchronization,
  	 * {@code SUPPORTS} is slightly different from no transaction at all,
  	 * as it defines a transaction scope that synchronization will apply for.
  	 * As a consequence, the same resources (JDBC Connection, Hibernate Session, etc)
  	 * will be shared for the entire specified scope. Note that this depends on
  	 * the actual synchronization configuration of the transaction manager.
  	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
  	 */
  	SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),
  ```

  主要关注`Support a current transaction, execute non-transactionally if none exists.`从字面意思来看，就是如果当前环境有事务，我就加入到当前事务；如果没有事务，我就以非事务的方式执行。从这方面来看，貌似我们加不加这一行其实都没啥差别。

  划重点：**NOTE**，对于一个带有事务同步的管理器来说，这里有一丢丢的小区别啦。（所以大家在读注释的时候，一定要看这个Note.往往这里面会有好东西给我们，就相当于我们的大喇叭！）

  这个同步事务管理器定义了一个事务同步的一个范围，如果加了这个注解，那么就等同于我让你来管我啦，你里面的资源我想用就可以用(JDBC Connection, Hibernate Session).

## 结论1

  > SUPPORTS 标注的方法可以获取和当前事务环境一致的 Connection 或 Session，不使用的话一定是一个新的连接；  
  再注意下面又一个**NOTE**，即便上面的配置加入了，但是`事务管理器的实际同步配置`会影响到真实的执行到底是否会用你。看它的说明：`@see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization`.

  ```java
  	/**
  	 * Set when this transaction manager should activate the thread-bound
  	 * transaction synchronization support. Default is "always".
  	 * <p>Note that transaction synchronization isn't supported for
  	 * multiple concurrent transactions by different transaction managers.
  	 * Only one transaction manager is allowed to activate it at any time.
  	 * @see #SYNCHRONIZATION_ALWAYS
  	 * @see #SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
  	 * @see #SYNCHRONIZATION_NEVER
  	 * @see TransactionSynchronizationManager
  	 * @see TransactionSynchronization
  	 */
  	public final void setTransactionSynchronization(int transactionSynchronization) {
  		this.transactionSynchronization = transactionSynchronization;
  	}
  ```

  描述信息只是说在同一个事务管理器才能起作用，并没有什么实际意义，我们来看一下`TransactionSynchronization`具体的内容：

  ```java
  package org.springframework.transaction.support;
  
  import java.io.Flushable;
  
  public interface TransactionSynchronization extends Flushable {
  
  	/** Completion status in case of proper commit. */
  	int STATUS_COMMITTED = 0;
  
  	/** Completion status in case of proper rollback. */
  	int STATUS_ROLLED_BACK = 1;
  
  	/** Completion status in case of heuristic mixed completion or system errors. */
  	int STATUS_UNKNOWN = 2;
  
  	/**
  	 * Suspend this synchronization.
  	 * Supposed to unbind resources from TransactionSynchronizationManager if managing any.
  	 * @see TransactionSynchronizationManager#unbindResource
  	 */
  	default void suspend() {
  	}
  
  	/**
  	 * Resume this synchronization.
  	 * Supposed to rebind resources to TransactionSynchronizationManager if managing any.
  	 * @see TransactionSynchronizationManager#bindResource
  	 */
  	default void resume() {
  	}
  
  	/**
  	 * Flush the underlying session to the datastore, if applicable:
  	 * for example, a Hibernate/JPA session.
  	 * @see org.springframework.transaction.TransactionStatus#flush()
  	 */
  	@Override
  	default void flush() {
  	}
  
  	/**
  	 * ...
  	 */
  	default void beforeCommit(boolean readOnly) {
  	}
  
  	/**
  	 * ...
  	 */
  	default void beforeCompletion() {
  	}
  
  	/**
  	 * ...
  	 */
  	default void afterCommit() {
  	}
  
  	/**
  	 * ...
  	 */
  	default void afterCompletion(int status) {
  	}
  }
  ```

  事务管理器可以通过`org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization(int)`来对当前事务进行行为干预，比如将它设置为1，可以执行事务回调，设置为2，表示出错了，但是如果没有加入`PROPAGATION.SUPPORTS`注解的话，即便你在当前事务中，你也不能对我进行操作和变更。

## 结论2

  > 添加`PROPAGATION.SUPPORTS`之后，当前查询中可以对当前的事务进行设置回调动作，不添加就不行。

# 源码下载

[Github 传送门](https://github.com/Isaac-Zhang/expensive-shop)  
[Gitee 传送门](https://gitee.com/IsaacZhang/expensive-shop)

# 下节预告

下一节我们将继续开发商品详情展示以及商品评价业务，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！