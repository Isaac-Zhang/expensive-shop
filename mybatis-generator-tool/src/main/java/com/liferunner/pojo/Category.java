package com.liferunner.pojo;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Table(name = "category")
public class Category {
    /**
     * 主键 分类id主键
     */
    @Id
    private Integer id;

    /**
     * 分类名称 分类名称
     */
    private String name;

    /**
     * 分类类型 分类得类型，
1:一级大分类
2:二级分类
3:三级小分类
     */
    private Integer type;

    /**
     * 父id 父id 上一级依赖的id，1级分类则为0，二级三级分别依赖上一级
     */
    @Column(name = "father_id")
    private Integer fatherId;

    /**
     * 图标 logo
     */
    private String logo;

    /**
     * 口号
     */
    private String slogan;

    /**
     * 分类图
     */
    @Column(name = "cat_image")
    private String catImage;

    /**
     * 背景颜色
     */
    @Column(name = "bg_color")
    private String bgColor;
}