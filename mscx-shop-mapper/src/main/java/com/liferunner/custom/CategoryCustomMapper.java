package com.liferunner.custom;

import com.liferunner.dto.SecondSubCategoryResponseDTO;

import java.util.List;

/**
 * CategoryCustomMapper for : 自定义分类mapper
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/14
 */
public interface CategoryCustomMapper {
    List<SecondSubCategoryResponseDTO> getSubCategorys(Integer parentId);
}
