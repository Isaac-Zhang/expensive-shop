package com.liferunner.api.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.MerchantOrderRequestDTO;
import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.dto.ShopcartRequestDTO;
import com.liferunner.enums.OrderStatusEnum;
import com.liferunner.enums.PayTypeEnum;
import com.liferunner.service.IOrderService;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;
    private final RedisUtils redisUtils;

    @PostMapping("/create")
    @ApiOperation(notes = "创建订单API", value = "创建订单API")
    public JsonResponse create(@RequestBody OrderRequestDTO orderRequestDTO,
        HttpServletRequest request,
        HttpServletResponse response) {
        if (!PayTypeEnum.WECHAT.key.equals(orderRequestDTO.getPayMethod()) ||
            !PayTypeEnum.WECHAT.key.equals(orderRequestDTO.getPayMethod())) {
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

        //获取redis中的购物车信息
        String shopcartFromRedisStr = redisUtils.get(SHOPCART_COOKIE_NAME + ":" + orderRequestDTO.getUserId());
        if (StringUtils.isBlank(shopcartFromRedisStr)) {
            log.error("从redis中获取购物车数据为空！！{}", JSON.toJSONString(orderRequestDTO));
            return JsonResponse.errorMsg("购物车数据为空，请重新添加后尝试提交。");
        }

        List<ShopcartRequestDTO> shopcartRequestDTOList = JSON.parseArray(shopcartFromRedisStr,
            ShopcartRequestDTO.class);

        val orderResponseDTO = this.orderService.createOrder(shopcartRequestDTOList,orderRequestDTO);
        String orderId = orderResponseDTO.getOrderId();

        //TODO : Redis准备就绪之后，需要从Redis中删除掉已经付款的商品信息，并且同步需要删除前端cookie中的商品
        //暂时屏蔽CookieTools.setCookie(request, response, SHOPCART_COOKIE_NAME, "", true);

        // TODO: 发送请求到支付中心付款
        val merchantOrderRequestDTO = orderResponseDTO.getMerchantOrderRequestDTO();
        //为了测试支付金额修改为1分钱
       /*  //TODO：请求支付中心，后期添加
       merchantOrderRequestDTO.setAmount(1);
        merchantOrderRequestDTO.setReturnUrl(PAYMENT_RETURN_URL);
        HttpHeaders httpHeaders
                = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("imoocUserId", PAYMENT_USER_ID);
        httpHeaders.add("password", PAYMENT_PASSWORD);
        HttpEntity<MerchantOrderRequestDTO> entity =
                new HttpEntity<>(merchantOrderRequestDTO, httpHeaders);

        ResponseEntity<JsonResponse> responseEntity =
                restTemplate.postForEntity(PAYMENT_SERVER_URL,
                        entity,
                        JsonResponse.class);
        JsonResponse paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != 200) {
            log.error("发送错误：{}", paymentResult.getMessage());
            return JsonResponse.errorMsg("支付中心订单创建失败，请联系管理员！");
        }*/
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

    @ApiOperation(value = "根据订单号查询订单支付结果", tags = "根据订单号查询订单支付结果")
    @PostMapping("/getPaidResult")
    public JsonResponse getPaidResult(@RequestParam String orderId) {
        val paidResult = this.orderService.getPaidResult(orderId);
        return JsonResponse.ok(paidResult);
    }
}
