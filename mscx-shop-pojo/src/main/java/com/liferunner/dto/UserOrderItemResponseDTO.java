package com.liferunner.dto;

import lombok.*;

/**
 * UserOrderItemResponseDTO for : 用户订单返回对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserOrderItemResponseDTO {

    private String productId;
    private String productImage;
    private String productName;
    private String productSpecName;
    private Integer buyCounts;
    private Integer price;
}
