package com.liferunner.pojo;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "order_items")
public class OrderProducts {
    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 归属订单id
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 商品id
     */
    @Column(name = "item_id")
    private String productId;

    /**
     * 商品图片
     */
    @Column(name = "item_img")
    private String productImg;

    /**
     * 商品名称
     */
    @Column(name = "item_name")
    private String productName;

    /**
     * 规格id
     */
    @Column(name = "item_spec_id")
    private String productSpecId;

    /**
     * 规格名称
     */
    @Column(name = "item_spec_name")
    private String productSpecName;

    /**
     * 成交价格
     */
    private Integer price;

    /**
     * 购买数量
     */
    @Column(name = "buy_counts")
    private Integer buyCounts;
}