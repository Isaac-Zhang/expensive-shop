package com.liferunner.service;

import com.liferunner.dto.CategoryResponseDTO;
import com.liferunner.dto.SecondSubCategoryResponseDTO;

import java.util.List;

/**
 * ICategoryService for : 分类service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/13
 */
public interface ICategoryService {

    /**
     * 获取所有有效的一级分类（根节点）
     *
     * @return
     */
    List<CategoryResponseDTO> getAllRootCategorys();

    /**
     * 根据一级分类获取子分类
     *
     * @param parentId 一级分类id
     * @return 子分类list
     */
    List<SecondSubCategoryResponseDTO> getAllSubCategorys(Integer parentId);
}
