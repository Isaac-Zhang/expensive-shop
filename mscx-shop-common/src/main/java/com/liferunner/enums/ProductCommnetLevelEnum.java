package com.liferunner.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ProductCommnetLevelEnum for : 商品评价等级enum
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@AllArgsConstructor
public enum ProductCommnetLevelEnum {

    GOOD(1, "好评"),
    NORMAL(2, "中评"),
    BAD(3, "差评");

    public Integer type;
    private String desc;
}
