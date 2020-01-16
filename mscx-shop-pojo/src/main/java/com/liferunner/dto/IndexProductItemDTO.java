package com.liferunner.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * IndexProductItemDTO for : 首页推荐商品元素DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
@Data
@ToString
public class IndexProductItemDTO {

    private String productId;
    private String productName;
    private String productMainImageUrl;
    private Date productCreateTime;
}
