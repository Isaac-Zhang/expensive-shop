package com.liferunner.enums;

import lombok.AllArgsConstructor;

/**
 * PayTypeEnum for : 支付类型enum
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@AllArgsConstructor
public enum PayTypeEnum {
    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝");

    public Integer key;
    public String value;
}
