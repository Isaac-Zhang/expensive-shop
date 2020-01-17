package com.liferunner.api.controller;

import com.liferunner.enums.BooleanEnum;
import com.liferunner.pojo.Orders;
import com.liferunner.service.IOrderService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseController for : controller 基类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@Controller
public class BaseController {

    /**
     * 默认展示第1页
     */
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    /**
     * 默认每页展示10条数据
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认购物车cookie名
     */
    public static final String SHOPCART_COOKIE_NAME = "shopcart";

    /**
     * 支付中心URL
     */
    public static final String PAYMENT_SERVER_URL = "http://api.z.mukewang"
        + ".com/foodie-dev-api/orders/notifyMerchantOrderPaid";

    /**
     * 支付成功回调URL
     */
    public static final String PAYMENT_RETURN_URL = "http://localhost:8088/orders/notifyOrderFromWechat";

    /**
     * 支付中心账户
     */
    public static final String PAYMENT_USER_ID = "3120391-497095098";//zhangpan

    /**
     * 支付中心账户密码
     */
    public static final String PAYMENT_PASSWORD = "1i12-pow0-pdwq-jgg8";
    /**
     * redis中存储用户token
     */
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    /**
     * 文件上传路径
     */
    public static final String IMG_FACE_UPLOAD_PATH =
        File.separator + "promotion" +
            File.separator + "sources" +
            File.separator + "expensive-shop" +
            File.separator + "face-img";
    /**
     * 图片存储的web地址前缀
     */
    public static final String IMG_FACE_BASE_WEB_URL = "http://127.0.0.1:8088/";

    public static Map<String, String> getErrorsMap(BindingResult result) {
        Map<String, String> resultMap = new HashMap<>();
        result.getFieldErrors().stream().forEach(e -> {
            val field = e.getField();
            val error = e.getDefaultMessage();
            resultMap.put(field, String.valueOf(error));
        });
        return resultMap;
    }

    @Autowired
    private IOrderService orderService;

    /***
     * 验证当前订单是否属于当前用户
     * 防止恶意更新
     */
    public boolean validateRelationshipUserAndOrder(String userId, String orderId) {
        Orders order = this.orderService.getOrderById(orderId);
        if (null != order && order.getUserId().equalsIgnoreCase(userId) && order.getIsDelete()
            .equals(BooleanEnum.FALSE.type)) {
            return true;
        }
        return false;
    }
}
