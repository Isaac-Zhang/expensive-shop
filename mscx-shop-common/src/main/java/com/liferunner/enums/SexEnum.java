package com.liferunner.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SexEnum for : sex enum
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/5
 */
@AllArgsConstructor
public enum SexEnum {
    WOMAN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    public final Integer type;
    public final String value;
}
