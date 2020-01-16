package com.liferunner.dto;

import lombok.Data;
import lombok.ToString;

/**
 * ThirdSubCategoryResponseDTO for : 第三季分类返回对象
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/14
 */
@Data
@ToString
public class ThirdSubCategoryResponseDTO {

    /**
     * 主键
     */
    private Integer subId;

    /**
     * 分类名称
     */
    private String subName;

    /**
     * 分类类型 1:一级大分类 2:二级分类 3:三级小分类
     */
    private Integer subType;

    /**
     * 父id
     */
    private Integer subParentId;
}
