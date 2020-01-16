package com.liferunner.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * UserOrderResponseDTO for : 我的订单列表
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserOrderResponseDTO {

    private String orderId;
    private Date createdTime;
    private Integer payMethod;
    private Integer realPayAmount;
    private Integer postAmount;
    private Integer isComment;
    private Integer orderStatus;

    private List<UserOrderItemResponseDTO> orderItemList;
}
