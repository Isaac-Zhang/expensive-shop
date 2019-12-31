package com.liferunner.api.controller;

import com.liferunner.dto.ProductCommentLevelCountsDTO;
import com.liferunner.dto.ProductDetailResponseDTO;
import com.liferunner.enums.ProductCommnetLevelEnum;
import com.liferunner.service.IProductService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ProductController for : 商品controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@RestController
@Slf4j
@RequestMapping("/product")
@Api(value = "商品服务", tags = "查询商品相关接口")
public class ProductController extends BaseController {

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
        val productDetailResponseDTO = ProductDetailResponseDTO
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

        val commentLevelCountsDTO = ProductCommentLevelCountsDTO
            .builder()
            .totalCounts(totalCounts)
            .goodCounts(goodCounts)
            .normalCounts(normalCounts)
            .badCounts(badCounts)
            .build();
        return JsonResponse.ok(commentLevelCountsDTO);
    }

    @GetMapping("/comments")
    @ApiOperation(value = "查询商品评价", notes = "根据商品id查询商品评价")
    public JsonResponse getProductComment(
        @ApiParam(name = "pid", value = "商品id", required = true)
        @RequestParam String pid,
        @ApiParam(name = "level", value = "评价级别", required = false, example = "0")
        @RequestParam Integer level,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (StringUtils.isBlank(pid)) {
            return JsonResponse.errorMsg("商品id不能为空！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============查询商品评价:{}==============", pid);

        val productComments = this.productService.getProductComments(pid, level, pageNumber, pageSize);

        return JsonResponse.ok(productComments);
    }

    @GetMapping("/search")
    @ApiOperation(value = "查询商品信息列表", notes = "查询商品信息列表")
    public JsonResponse searchProductList(
        @ApiParam(name = "keyword", value = "搜索关键词", required = true)
        @RequestParam String keyword,
        @ApiParam(name = "sortby", value = "排序方式", required = false)
        @RequestParam String sortby,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (StringUtils.isBlank(keyword)) {
            return JsonResponse.errorMsg("搜索关键词不能为空！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============根据关键词:{} 搜索列表==============", keyword);

        val searchResult = this.productService.searchProductList(keyword, sortby, pageNumber, pageSize);
        return JsonResponse.ok(searchResult);
    }

    @GetMapping("/searchByCategoryId")
    @ApiOperation(value = "查询商品信息列表", notes = "根据商品分类查询商品列表")
    public JsonResponse searchProductListByCategoryId(
        @ApiParam(name = "categoryId", value = "商品分类id", required = true, example = "0")
        @RequestParam Integer categoryId,
        @ApiParam(name = "sortby", value = "排序方式", required = false)
        @RequestParam String sortby,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (null == categoryId || categoryId == 0) {
            return JsonResponse.errorMsg("分类id错误！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============根据分类:{} 搜索列表==============", categoryId);

        val searchResult = this.productService.searchProductList(categoryId, sortby, pageNumber, pageSize);
        return JsonResponse.ok(searchResult);
    }
}
