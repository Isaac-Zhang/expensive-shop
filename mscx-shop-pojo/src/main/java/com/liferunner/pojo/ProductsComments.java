package com.liferunner.pojo;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "items_comments")
public class ProductsComments {
    /**
     * id主键
     */
    @Id
    private String id;

    /**
     * 用户id 用户名须脱敏
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 商品id
     */
    @Column(name = "item_id")
    private String productId;

    /**
     * 商品名称
     */
    @Column(name = "item_name")
    private String productName;

    /**
     * 商品规格id 可为空
     */
    @Column(name = "item_spec_id")
    private String productSpecId;

    /**
     * 规格名称 可为空
     */
    @Column(name = "spec_name")
    private String specName;

    /**
     * 评价等级 1：好评 2：中评 3：差评
     */
    @Column(name = "comment_level")
    private Integer commentLevel;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private Date updatedTime;
}