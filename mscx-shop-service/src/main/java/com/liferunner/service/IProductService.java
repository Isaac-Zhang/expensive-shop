package com.liferunner.service;

import com.liferunner.dto.IndexProductDTO;
import com.liferunner.dto.ProductCommentLevelCountsDTO;
import com.liferunner.pojo.Products;
import com.liferunner.pojo.ProductsImg;
import com.liferunner.pojo.ProductsParam;
import com.liferunner.pojo.ProductsSpec;

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
     * @param pid 商品id
     * @param level 评价等级
     * @return 评价数
     */
    Integer countProductCommentLevel(String pid,Integer level);
}
