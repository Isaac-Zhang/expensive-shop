package com.liferunner.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * IndexProductDTO for : 首页推荐商品
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
@Data
@ToString
public class IndexProductDTO {

    private Integer rootCategoryId;
    private String rootCategoryName;
    private String slogan;
    private String categoryImage;
    private String bgColor;

    private List<IndexProductItemDTO> productItemList;
}
