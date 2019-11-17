package com.liferunner.service;

import com.liferunner.dto.IndexProductDTO;
import com.liferunner.dto.ProductCommentLevelCountsDTO;
import com.liferunner.dto.ShopcartResponseDTO;
import com.liferunner.pojo.Products;
import com.liferunner.pojo.ProductsImg;
import com.liferunner.pojo.ProductsParam;
import com.liferunner.pojo.ProductsSpec;
import com.liferunner.utils.CommonPagedResult;

import java.util.List;

/**
 * IProductService for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
public interface IProductService {

    /**
     * 根据一级分类id获取首页推荐的商品list
     *
     * @param rootCategoryId 一级分类id
     * @return 商品list
     */
    List<IndexProductDTO> getIndexProductDtoList(Integer rootCategoryId);

    /**
     * 根据商品id查询商品
     *
     * @param pid 商品id
     * @return 商品主信息
     */
    Products findProductByPid(String pid);

    /**
     * 根据商品id查询商品图片
     *
     * @param pid 商品id
     * @return 图片list
     */
    List<ProductsImg> getProductImgsByPid(String pid);

    /**
     * 根据商品id查询商品规格
     *
     * @param pid 商品id
     * @return 规格list
     */
    List<ProductsSpec> getProductSpecsByPid(String pid);

    /**
     * 根据商品id查询商品参数
     *
     * @param pid 商品id
     * @return 参数
     */
    ProductsParam findProductParamByPid(String pid);

    /**
     * 根据商品id 查询评价总数
     *
     * @param pid   商品id
     * @param level 评价等级
     * @return 评价数
     */
    Integer countProductCommentLevel(String pid, Integer level);

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

    /**
     * 根据规格id列表查询购物车新数据
     *
     * @param specIds 规格ids
     * @return 新数据
     */
    List<ShopcartResponseDTO> refreshShopcart(String specIds);

    /**
     * 根据商品规格ids查询所有规格
     *
     * @param specIds
     * @return
     */
    List<ProductsSpec> getProductSpecByIds(String specIds);

    /**
     * 下单之后扣减库存
     * @param specId
     * @param buyNumber
     * @return
     */
    void decreaseProductSpecStock(String specId, Integer buyNumber);
}
