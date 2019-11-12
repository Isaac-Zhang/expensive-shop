package com.liferunner.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BooleanEnum for : display true or false
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@AllArgsConstructor
public enum BooleanEnum {
    FALSE(0, "否"),
    TRUE(1, "是");

    public final Integer type;
    public final String value;
}
