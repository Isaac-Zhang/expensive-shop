package com.liferunner.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * SlideAdResponseDTO for : 轮播广告返回实体
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "轮播广告返回DTO", description = "轮播广告返回DTO")
public class SlideAdResponseDTO{
    /**
     * 主键
     */
    private String id;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     *  背景颜色
     */
    private String backgroundColor;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 商品分类id
     */
    private String catId;

    /**
     * 图片跳转URL
     */
    private String imageLinkUrl;

    /**
     * 轮播图类型 用于判断，可以根据商品id或者分类进行页面跳转，1：商品 2：分类 3：链接url
     */
    private Integer type;

    /**
     * 轮播图展示顺序 轮播图展示顺序，从小到大
     */
    private Integer sort;

    /**
     * 是否展示 是否展示，1：展示    0：不展示
     */
    private Integer isShow;

    /**
     * 创建时间 创建时间
     */
    private Date createTime;

    /**
     * 更新时间 更新
     */
    private Date updateTime;
}