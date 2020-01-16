package com.liferunner.dto;

import lombok.*;

/**
 * OrderRequestDTO for : 创建订单DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class OrderRequestDTO {

    private String userId;
    private String productSpecIds;
    private String addressId;
    private Integer payMethod;
    private String orderComment;
}
