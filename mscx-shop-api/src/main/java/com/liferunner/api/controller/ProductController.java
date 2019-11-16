package com.liferunner.api.controller;

import com.liferunner.dto.ProductCommentLevelCountsDTO;
import com.liferunner.dto.ProductDetailResponseDTO;
import com.liferunner.enums.ProductCommnetLevelEnum;
import com.liferunner.service.IProductService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ProductController for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@RestController
@Slf4j
@RequestMapping("/product")
@Api(value = "商品服务", tags = "查询商品相关接口")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/detail/{pid}")
    @ApiOperation(value = "根据商品id查询详情", notes = "根据商品id查询详情")
    public JsonResponse findProductDetailByPid(
            @ApiParam(name = "pid", value = "商品id", required = true)
            @PathVariable String pid) {
        if (StringUtils.isBlank(pid)) {
            return JsonResponse.errorMsg("商品id不能为空！");
        }
        val product = this.productService.findProductByPid(pid);
        val productImgList = this.productService.getProductImgsByPid(pid);
        val productSpecList = this.productService.getProductSpecsByPid(pid);
        val productParam = this.productService.findProductParamByPid(pid);
        val productDetailResponseDTO = new ProductDetailResponseDTO()
                .builder()
                .products(product)
                .productsImgList(productImgList)
                .productsSpecList(productSpecList)
                .productsParam(productParam)
                .build();
        log.info("============查询到商品详情:{}==============", productDetailResponseDTO);

        return JsonResponse.ok(productDetailResponseDTO);
    }

    @GetMapping("/commentLevel")
    @ApiOperation(value = "查询商品评价数", notes = "根据商品id查询评价等级")
    public JsonResponse countCommentLevel(
            @ApiParam(name = "pid", value = "商品id", required = true)
            @RequestParam String pid) {
        if (StringUtils.isBlank(pid)) {
            return JsonResponse.errorMsg("商品id不能为空！");
        }
        Integer goodCounts = this.productService.countProductCommentLevel(pid
                , ProductCommnetLevelEnum.GOOD.type);
        Integer normalCounts = this.productService.countProductCommentLevel(pid
                , ProductCommnetLevelEnum.NORMAL.type);
        Integer badCounts = this.productService.countProductCommentLevel(pid
                , ProductCommnetLevelEnum.BAD.type);

        Integer totalCounts = goodCounts + normalCounts + badCounts;
        log.info("============查询到商品评价总数:{}==============", totalCounts);

        val commentLevelCountsDTO = new ProductCommentLevelCountsDTO()
                .builder()
                .totalCounts(totalCounts)
                .goodCounts(goodCounts)
                .normalCounts(normalCounts)
                .badCounts(badCounts)
                .build();
        return JsonResponse.ok(commentLevelCountsDTO);
    }
}
