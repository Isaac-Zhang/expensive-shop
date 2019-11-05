package com.liferunner.pojo;

import java.util.Date;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "`items_img`")
public class ItemsImg {
    /**
     * 图片主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 商品外键id 商品外键id
     */
    @Column(name = "`item_id`")
    private String itemId;

    /**
     * 图片地址 图片地址
     */
    @Column(name = "`url`")
    private String url;

    /**
     * 顺序 图片顺序，从小到大
     */
    @Column(name = "`sort`")
    private Integer sort;

    /**
     * 是否主图 是否主图，1：是，0：否
     */
    @Column(name = "`is_main`")
    private Integer isMain;

    /**
     * 创建时间
     */
    @Column(name = "`created_time`")
    private Date createdTime;

    /**
     * 更新时间
     */
    @Column(name = "`updated_time`")
    private Date updatedTime;
}