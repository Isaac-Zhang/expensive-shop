package com.liferunner.dto;

import lombok.*;

/**
 * MerchantOrderRequestDTO for : 请求支付中心对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MerchantOrderRequestDTO {
    /**
     * 商户订单号
     */
    private String merchantOrderId;
    /**
     * 商户方的发起用户的主键id
     */
    private String merchantUserId;
    /**
     * 实际支付总金额
     */
    private Integer amount;
    /**
     * 支付方式 1:微信   2:支付宝
     */
    private Integer payMethod;
    /**
     * 支付成功后的回调地址
     */
    private String returnUrl;
}
