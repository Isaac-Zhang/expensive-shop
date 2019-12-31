package com.liferunner.dto;

import java.io.Serializable;
import lombok.*;

/**
 * ShopcartRequestDTO for : 前端传入的购物车对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ShopcartRequestDTO implements Serializable {
    private String productId;
    private String productImgUrl;
    private String productName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private Integer priceDiscount;
    private Integer priceNormal;
}
