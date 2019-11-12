package com.liferunner.pojo;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "slide_ads")
public class SlideAds {
    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 图片 图片地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 背景色 背景颜色
     */
    @Column(name = "background_color")
    private String backgroundColor;

    /**
     * 商品id 商品id
     */
    @Column(name = "item_id")
    private String itemId;

    /**
     * 商品分类id 商品分类id
     */
    @Column(name = "cat_id")
    private String catId;

    /**
     * 图片跳转URL
     */
    @Column(name = "image_link_url")
    private String imageLinkUrl;

    /**
     * 轮播图类型 轮播图类型，用于判断，可以根据商品id或者分类进行页面跳转，1：商品 2：分类
     */
    private Integer type;

    /**
     * 轮播图展示顺序 轮播图展示顺序，从小到大
     */
    private Integer sort;

    /**
     * 是否展示 是否展示，1：展示    0：不展示
     */
    @Column(name = "is_show")
    private Integer isShow;

    /**
     * 创建时间 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间 更新
     */
    @Column(name = "update_time")
    private Date updateTime;
}