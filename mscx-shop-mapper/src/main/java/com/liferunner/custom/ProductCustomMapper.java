package com.liferunner.custom;

import com.liferunner.dto.IndexProductDTO;
import com.liferunner.dto.ProductCommentDTO;
import com.liferunner.dto.SearchProductDTO;
import com.liferunner.dto.ShopcartResponseDTO;
import com.liferunner.pojo.ProductsSpec;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ProductCustomMapper for : 自定义商品Mapper
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
public interface ProductCustomMapper {

    /***
     * 根据一级分类查询商品
     *
     * @param paramMap 传递一级分类（map传递多参数）
     * @return java.util.List<com.liferunner.dto.IndexProductDTO>
     */
    List<IndexProductDTO> getIndexProductDtoList(@Param("paramMap") Map<String, Integer> paramMap);

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

    List<SearchProductDTO> searchProductList(@Param("paramMap") Map<String, Object> paramMap);

    List<SearchProductDTO> searchProductListByCategoryId(@Param("paramMap") Map<String, Object> paramMap);

    List<ShopcartResponseDTO> refreshShopcart(@Param("specIdList") List<String> specIdList);

    List<ProductsSpec> getAllProductSpec(@Param("specIdList") List<String> specIds);

    Integer decreaseProductSpecStock(@Param("specId") String specId, @Param("buyNumber") Integer buyNumber);
}
