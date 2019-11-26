package com.liferunner.service;

import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.dto.OrderResponseDTO;
import com.liferunner.enums.OrderStatusEnum;
import com.liferunner.pojo.OrderStatus;
import com.liferunner.pojo.Orders;

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
    OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);

    /**
     * 订单支付成功，更新订单状态
     *
     * @param orderId
     * @param orderStatus
     */
    void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 根据订单id查询订单支付结果
     *
     * @param orderId
     * @return
     */
    OrderStatus getPaidResult(String orderId);

    /**
     * 定时关闭超时的订单
     */
    void AutoCloseOvertimeOrder();

    Orders getOrderById(String orderId);

    int deleteOrder(String orderId);
}
