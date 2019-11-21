# 上文回顾

[上节](https://segmentfault.com/a/1190000021047932) 我们实现了根据搜索关键词查询商品列表和根据商品分类查询，并且使用到了`mybatis-pagehelper`插件，讲解了如何使用插件来帮助我们快速实现分页数据查询。本文我们将继续开发商品详情页面和商品留言功能的开发。

# 需求分析



# 首页商品列表|IndexProductList

## 开发梳理
- 

## 编码实现
### 根据一级分类查询



#### Custom Mapper实现

#### Service实现

#### Controller实现
#### Test API

# 福利讲解
有心的小伙伴肯定又注意到了，在Service中处理查询时，我一部分使用了`@Transactional(propagation = Propagation.SUPPORTS)`,一部分查询又没有添加事务，那么这两种方式有什么不一样呢？接下来，我们来揭开神秘的面纱。
# 源码下载
---

[Github 传送门](https://github.com/Isaac-Zhang/expensive-shop)  
[Gitee 传送门](https://gitee.com/IsaacZhang/expensive-shop)

# 下节预告
---

下一节我们将继续开发商品详情展示以及商品评价业务，在过程中使用到的任何开发组件，我都会通过专门的一节来进行介绍的，兄弟们末慌！

gogogo！