package com.liferunner.dto;

import com.liferunner.pojo.Products;
import com.liferunner.pojo.ProductsImg;
import com.liferunner.pojo.ProductsParam;
import com.liferunner.pojo.ProductsSpec;
import lombok.*;

import java.util.List;

/**
 * ProductDetailResponseDTO for : 商品详情页面展示DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
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
