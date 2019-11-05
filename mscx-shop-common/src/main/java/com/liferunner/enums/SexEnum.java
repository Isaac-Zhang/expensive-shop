package com.liferunner.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SexEnum for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/5
 */
@AllArgsConstructor
public enum SexEnum {
    woman(0, "女"),
    man(1, "男"),
    secret(2, "保密");

    public final Integer type;
    public final String value;
}
