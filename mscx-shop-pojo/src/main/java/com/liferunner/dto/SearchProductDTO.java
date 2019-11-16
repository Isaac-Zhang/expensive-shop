package com.liferunner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SearchProductDTO for : 查询商品结果DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
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
}
