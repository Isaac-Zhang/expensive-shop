package com.liferunner.api.controller;

import com.liferunner.dto.ShopcartRequestDTO;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ShopcartController for : 购物车controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@RestController
@Slf4j
@RequestMapping("/shopcart")
@Api(value = "购物车接口", tags = {"购物车接口实现"})
public class ShopcartController {

    @PostMapping("/add")
    public JsonResponse addToShopcart(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "shopcartRequestDTO", value = "购物车信息", required = true)
            @RequestBody ShopcartRequestDTO shopcartRequestDTO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // TODO: 需要实现用户权限校验
        if (StringUtils.isBlank(userId) || null == shopcartRequestDTO) {
            return JsonResponse.errorMsg("添加购物车参数错误!");
        }

        // TODO: 需要添加购物车信息到Redis缓存中,用于持久化和分布式集群数据同步
        log.info("当前需要添加到购物车的商品:{}", shopcartRequestDTO);
        return JsonResponse.ok();
    }
}
