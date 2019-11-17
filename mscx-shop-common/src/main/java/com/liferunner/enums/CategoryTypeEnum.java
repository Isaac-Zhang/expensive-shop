package com.liferunner.enums;

import lombok.AllArgsConstructor;

/**
 * CategoryTypeEnum for : 商品分类enum
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/13
 */
@AllArgsConstructor
public enum CategoryTypeEnum {
    ROOT(1, "一级分类"),
    FIRST(2, "二级分类"),
    SECOND(3, "三级分类");

    public final Integer type;
    public final String value;
}
