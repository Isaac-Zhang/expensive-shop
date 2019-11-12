package com.liferunner.service;

import com.liferunner.dto.SlideAdResponseDTO;
import com.liferunner.pojo.SlideAds;

import java.util.List;

/**
 * ISlideAdService for : 实现轮播广告操作接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
public interface ISlideAdService {

    /**
     * 查询所有可用广告并排序
     * @param isShow
     * @return
     */
    List<SlideAdResponseDTO> findAll(Integer isShow, String sortRanking);
}
