package com.liferunner.service.impl;

import com.liferunner.dto.SlideAdResponseDTO;
import com.liferunner.mapper.SlideAdsMapper;
import com.liferunner.pojo.SlideAds;
import com.liferunner.service.ISlideAdService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SlideAdServiceImpl for : 实现轮播广告操作
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@Service
@Slf4j
public class SlideAdServiceImpl implements ISlideAdService {

    // 注入mapper
    private final SlideAdsMapper slideAdsMapper;

    @Autowired
    public SlideAdServiceImpl(SlideAdsMapper slideAdsMapper) {
        this.slideAdsMapper = slideAdsMapper;
    }

    @Override
    public List<SlideAdResponseDTO> findAll(Integer isShow, String sortRanking) {
        Example example = new Example(SlideAds.class);
        //设置排序
        if (StringUtils.isBlank(sortRanking)) {
            example.orderBy("sort").asc();
        } else {
            example.orderBy("sort").desc();
        }
        val conditions = example.createCriteria();
        conditions.andEqualTo("isShow", isShow);
        val slideAdsList = this.slideAdsMapper.selectByExample(example);
        //声明返回对象
        List<SlideAdResponseDTO> slideAdResponseDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(slideAdsList)) {
            //赋值
            SlideAdResponseDTO dto;
            for (SlideAds slideAds : slideAdsList) {
                dto = new SlideAdResponseDTO();
                BeanUtils.copyProperties(slideAds, dto);
                slideAdResponseDTOList.add(dto);
            }
        }
        return slideAdResponseDTOList;
    }
}
