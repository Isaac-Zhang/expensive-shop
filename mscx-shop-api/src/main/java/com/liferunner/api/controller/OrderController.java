package com.liferunner.api.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.enums.OrderStatusEnum;
import com.liferunner.enums.PayTypeEnum;
import com.liferunner.service.IOrderService;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OrderController for : 订单相关controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@RestController
@Slf4j
@RequestMapping("/orders")
@Api(tags = "订单处理接口API", value = "订单controller")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController extends BaseController {

    private final IOrderService orderService;

    @PostMapping("/create")
    @ApiOperation(notes = "创建订单API", value = "创建订单API")
    public JsonResponse create(@RequestBody OrderRequestDTO orderRequestDTO,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (PayTypeEnum.WECHAT.key != orderRequestDTO.getPayMethod() ||
                PayTypeEnum.WECHAT.key != orderRequestDTO.getPayMethod()) {
            log.error("不支持的支付类型!{}", JSON.toJSONString(orderRequestDTO));
            return JsonResponse.errorMsg("不支持的支付类型!");
        }
        if (
                StringUtils.isBlank(orderRequestDTO.getUserId()) ||
                        StringUtils.isBlank(orderRequestDTO.getUserId()) ||
                        StringUtils.isBlank(orderRequestDTO.getUserId()) ||
                        StringUtils.isBlank(orderRequestDTO.getUserId())
        ) {
            log.error("支付参数错误!{}", JSON.toJSONString(orderRequestDTO));
            return JsonResponse.errorMsg("支付参数错误!");
        }
        String orderId = this.orderService.createOrder(orderRequestDTO);

        //TODO : Redis准备就绪之后，需要从Redis中删除掉已经付款的商品信息，并且同步需要删除前端cookie中的商品
        //暂时屏蔽CookieTools.setCookie(request, response, SHOPCART_COOKIE_NAME, "", true);
        return JsonResponse.ok(orderId);
    }

    @PostMapping("/notifyOrderFromWechat")
    @ApiOperation(value = "微信支付成功回调接口", notes = "微信支付成功回调接口")
    public JsonResponse notifyOrderFromWechat(
            @ApiParam(name = "orderId", value = "订单id")
            @RequestParam String orderId) {
        log.info("======微信支付成功回调：orderId = {}", orderId);
        this.orderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.key);
        log.info("======微信支付成功回调，rderId = {}，更新状态为：{}",
                orderId,
                OrderStatusEnum.WAIT_DELIVER.value);
        return JsonResponse.ok();
    }

}
