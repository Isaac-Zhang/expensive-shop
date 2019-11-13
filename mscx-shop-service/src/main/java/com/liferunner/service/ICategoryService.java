package com.liferunner.service;

import com.liferunner.dto.CategoryResponseDTO;

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
}
