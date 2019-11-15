package com.liferunner.api.controller;

import com.liferunner.service.ICategoryService;
import com.liferunner.service.IProductService;
import com.liferunner.service.ISlideAdService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @Autowired
    private IProductService productService;

    @GetMapping("/slideAds")
    @ApiOperation(value = "查询轮播广告", notes = "查询轮播广告接口")
    public JsonResponse findAllSlideList() {
        log.info("============查询所有轮播广告,isShow={},sortRanking={}=============="
                , 1, "desc");
        val slideAdsList = this.slideAdService.findAll(1, "desc");
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

    @GetMapping("/subCategorys/{parentId}")
    @ApiOperation(value = "查询子分类", notes = "根据一级分类id查询子分类")
    public JsonResponse findAllSubCategorys(
            @ApiParam(name = "parentId", value = "一级分类id", required = true)
            @PathVariable Integer parentId) {
        log.info("============查询id = {}的子分类==============", parentId);
        val categoryResponseDTOS = this.categoryService.getAllSubCategorys(parentId);
        if (CollectionUtils.isEmpty(categoryResponseDTOS)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        log.info("============子分类查询result：{}==============", categoryResponseDTOS);
        return JsonResponse.ok(categoryResponseDTOS);
    }

    @RequestMapping(value = "/findIndexProductItemList/{parentId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据一级分类查询首页展示商品", notes = "根据一级分类查询首页展示商品")
    public JsonResponse findIndexProductItemList(
            @ApiParam(name = "parentId", value = "一级分类id", required = true)
            @PathVariable Integer parentId) {
        val indexProductDtoList = this.productService.getIndexProductDtoList(parentId);
        if (CollectionUtils.isEmpty(indexProductDtoList)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        return JsonResponse.ok(indexProductDtoList);
    }
}
