package com.liferunner.service;

import com.liferunner.dto.IndexProductDTO;

import java.util.List;

/**
 * IProductService for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
public interface IProductService {

    /**
     * 根据一级分类id获取首页推荐的商品list
     * @param rootCategoryId 一级分类id
     * @return 商品list
     */
    List<IndexProductDTO> getIndexProductDtoList(Integer rootCategoryId);
}
