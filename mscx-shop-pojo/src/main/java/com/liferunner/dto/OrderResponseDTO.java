package com.liferunner.dto;

import java.util.List;
import lombok.*;

/**
 * OrderResponseDTO for : 创建订单返回对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderResponseDTO {
    private String orderId;
    private MerchantOrderRequestDTO merchantOrderRequestDTO;
    private List<ShopcartRequestDTO> paddingRemovedList;
}
