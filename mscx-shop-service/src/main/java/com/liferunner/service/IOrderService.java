package com.liferunner.service;

import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.enums.OrderStatusEnum;

/**
 * IOrderService for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
public interface IOrderService {

    /**
     * 创建订单
     *
     * @param orderRequestDTO
     */
    String createOrder(OrderRequestDTO orderRequestDTO);

    /**
     * 订单支付成功，更新订单状态
     * @param orderId
     * @param orderStatus
     */
    void updateOrderStatus(String orderId, Integer orderStatus);
}
