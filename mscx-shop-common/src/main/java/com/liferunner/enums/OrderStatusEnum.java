package com.liferunner.enums;

import lombok.AllArgsConstructor;

/**
 * CategoryTypeEnum for : 订单状态enum
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@AllArgsConstructor
public enum OrderStatusEnum {

	WAIT_PAY(10, "待付款"),
	WAIT_DELIVER(20, "已付款，待发货"),
	WAIT_RECEIVE(30, "已发货，待收货"),
	SUCCESS(40, "交易成功"),
	CLOSE(50, "交易关闭");

	public final Integer key;
	public final String value;
}
