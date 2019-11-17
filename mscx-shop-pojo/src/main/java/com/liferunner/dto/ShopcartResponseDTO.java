package com.liferunner.dto;

import lombok.*;

/**
 * ShopcartResponseDTO for : 刷新购物车数据对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShopcartResponseDTO {
    private String productId;
    private String productImgUrl;
    private String productName;
    private String specId;
    private String specName;
    private Integer priceDiscount;
    private Integer priceNormal;
}
