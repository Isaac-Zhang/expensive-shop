package com.liferunner.api.controller;

import com.liferunner.service.ICategoryService;
import com.liferunner.service.ISlideAdService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * IndexController for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页信息展示", tags = "首页信息展示接口")
@Slf4j
public class IndexController {

    @Autowired
    private ISlideAdService slideAdService;
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/slideAds")
    @ApiOperation(value = "查询轮播广告", notes = "查询轮播广告接口")
    public JsonResponse findAllSlideList(@RequestParam Integer isShow,
                                         @RequestParam String sortRanking) {
        log.info("============查询所有轮播广告,isShow={},sortRanking={}=============="
                , isShow, sortRanking);
        val slideAdsList = this.slideAdService.findAll(isShow, sortRanking);
        if (CollectionUtils.isEmpty(slideAdsList)) {
            log.info("============未查询到任何轮播广告==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============轮播广告查询result：{}=============="
                , slideAdsList);
        return JsonResponse.ok(slideAdsList);
    }

    @GetMapping("/rootCategorys")
    @ApiOperation(value = "查询一级分类", notes = "查询一级分类")
    public JsonResponse findAllRootCategorys() {
        log.info("============查询一级分类==============");
        val categoryResponseDTOS = this.categoryService.getAllRootCategorys();
        if (CollectionUtils.isEmpty(categoryResponseDTOS)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============一级分类查询result：{}==============", categoryResponseDTOS);
        return JsonResponse.ok(categoryResponseDTOS);
    }
}
