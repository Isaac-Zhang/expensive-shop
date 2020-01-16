package com.liferunner.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * SecondSubCategoryResponseDTO for : 二级分类DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/14
 */
@Data
@ToString
public class SecondSubCategoryResponseDTO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类类型 1:一级大分类 2:二级分类 3:三级小分类
     */
    private Integer type;

    /**
     * 父id
     */
    private Integer parentId;

    List<ThirdSubCategoryResponseDTO> thirdSubCategoryResponseDTOList;
}
