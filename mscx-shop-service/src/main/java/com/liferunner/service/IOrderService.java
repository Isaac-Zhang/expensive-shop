package com.liferunner.service;

import com.liferunner.dto.OrderRequestDTO;

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
}
