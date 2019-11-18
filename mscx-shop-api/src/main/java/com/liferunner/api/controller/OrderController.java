package com.liferunner.api.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.enums.PayTypeEnum;
import com.liferunner.service.IOrderService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public JsonResponse create(@RequestBody OrderRequestDTO orderRequestDTO) {
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
        return JsonResponse.ok(orderId);
    }

}
