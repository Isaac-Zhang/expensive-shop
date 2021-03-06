package com.liferunner.api.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.SecondSubCategoryResponseDTO;
import com.liferunner.service.ICategoryService;
import com.liferunner.service.IProductService;
import com.liferunner.service.ISlideAdService;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * IndexController for : 首页controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/12
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页信息controller", tags = "首页信息接口API")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndexController {

    private final ISlideAdService slideAdService;
    private final ICategoryService categoryService;
    private final IProductService productService;
    private final RedisUtils redisUtils;

    @GetMapping("/slideAds")
    @ApiOperation(value = "查询轮播广告", notes = "查询轮播广告接口")
    public JsonResponse findAllSlideList() {
        String slideAdsFromRedis = this.redisUtils.get("slides");
        if (StringUtils.isEmpty(slideAdsFromRedis)) {
            log.info("============查询所有轮播广告,isShow={},sortRanking={}=============="
                , 1, "desc");
            val slideAdsList = this.slideAdService.findAll(1, "desc");
            if (CollectionUtils.isEmpty(slideAdsList)) {
                log.info("============未查询到任何轮播广告==============");
                return JsonResponse.ok(Collections.EMPTY_LIST);
            }
            log.info("============轮播广告查询result：{}=============="
                , slideAdsList);
            // 存储轮播广告到redis
            this.redisUtils.set("slides", JSON.toJSONString(slideAdsList));
            return JsonResponse.ok(slideAdsList);
        }
        log.info("============轮播广告查询来自于Redis：{}=============="
            , slideAdsFromRedis);
        return JsonResponse.ok(JSON.parse(slideAdsFromRedis));
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
        @ApiParam(name = "parentId", value = "一级分类id", required = true, example = "0")
        @PathVariable Integer parentId) {
        log.info("============查询id = {}的子分类==============", parentId);
        val subCatsFromRedisStr = redisUtils.get("subCategory:" + parentId);
        List<SecondSubCategoryResponseDTO> categoryResponseDTOS = Collections.EMPTY_LIST;
        if (StringUtils.isBlank(subCatsFromRedisStr)) {
            categoryResponseDTOS = this.categoryService.getAllSubCategorys(parentId);
            if (CollectionUtils.isEmpty(categoryResponseDTOS)) {
                // 防止Redis缓存穿透
                redisUtils.set("subCategory:" + parentId, JSON.toJSONString(categoryResponseDTOS), 5 * 60);
                log.info("============未查询到任何分类==============");
                return JsonResponse.ok(Collections.EMPTY_LIST);
            } else {
                // 如果查到值，正常设置redis
                redisUtils.set("subCategory:" + parentId, JSON.toJSONString(categoryResponseDTOS));
            }
            log.info("============子分类查询result：{}==============", categoryResponseDTOS);
        }
        categoryResponseDTOS = JSON.parseArray(subCatsFromRedisStr, SecondSubCategoryResponseDTO.class);
        return JsonResponse.ok(categoryResponseDTOS);
    }

    @RequestMapping(value = "/findIndexProductItemList/{parentId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据一级分类查询首页展示商品", notes = "根据一级分类查询首页展示商品")
    public JsonResponse findIndexProductItemList(
        @ApiParam(name = "parentId", value = "一级分类id", required = true, example = "0")
        @PathVariable Integer parentId) {
        val indexProductDtoList = this.productService.getIndexProductDtoList(parentId);
        if (CollectionUtils.isEmpty(indexProductDtoList)) {
            log.info("============未查询到任何分类==============");
            return JsonResponse.ok(Collections.EMPTY_LIST);
        }
        return JsonResponse.ok(indexProductDtoList);
    }

}
