package com.liferunner.service.impl;

import com.liferunner.dto.MerchantOrderRequestDTO;
import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.dto.OrderResponseDTO;
import com.liferunner.enums.BooleanEnum;
import com.liferunner.enums.OrderStatusEnum;
import com.liferunner.mapper.OrderProductsMapper;
import com.liferunner.mapper.OrderStatusMapper;
import com.liferunner.mapper.OrdersMapper;
import com.liferunner.pojo.OrderProducts;
import com.liferunner.pojo.OrderStatus;
import com.liferunner.pojo.Orders;
import com.liferunner.pojo.ProductsSpec;
import com.liferunner.service.IOrderService;
import com.liferunner.service.IProductService;
import com.liferunner.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * OrderServiceImpl for : 订单service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/17
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements IOrderService {
    private final OrdersMapper ordersMapper;
    private final OrderProductsMapper orderProductsMapper;
    private final Sid sid;
    private final IUserService userService;
    private final IProductService productService;
    private final OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        String orderId = sid.next();
        String userId = orderRequestDTO.getUserId();
        val addressId = orderRequestDTO.getAddressId();
        val payMethod = orderRequestDTO.getPayMethod();
        val orderComment = orderRequestDTO.getOrderComment();
        val productSpecIds = orderRequestDTO.getProductSpecIds();
        //默认全部包邮,邮寄费用为0
        Integer postFee = 0;
        Integer totalFee = 0;
        Integer realFee = 0;
        // 查询当前邮寄地址
        val userAddress = this.userService.findUserAddress(userId, addressId);
        // 根据商品规格ids查询所有商品信息到内存,用于循环
        val productsSpecList = this.productService.getProductSpecByIds(productSpecIds);
        for (ProductsSpec productsSpec : productsSpecList) {
            // TODO: 从Redis中获取实际SKU购买数量
            Integer buyNumber = 1;
            totalFee += productsSpec.getPriceNormal() * buyNumber;
            realFee += productsSpec.getPriceDiscount() * buyNumber;
            // 查询商品主图
            val productsImgList = this.productService.getProductImgsByPid(productsSpec.getProductId());
            val productsImg = productsImgList
                    .stream()
                    .filter(i -> i.getIsMain() == 1)
                    .findFirst()
                    .get();
            String mainImgUrl = productsImg.getUrl();
            val product = this.productService.findProductByPid(productsSpec.getProductId());
            // 准备插入订单子表对象
            val orderProduct = new OrderProducts()
                    .builder()
                    .id(sid.nextShort())
                    .orderId(orderId)
                    .productId(productsSpec.getProductId())
                    .productImg(mainImgUrl)
                    .productName(product.getProductName())
                    .productSpecId(productsSpec.getId())
                    .productSpecName(productsSpec.getName())
                    .price(productsSpec.getPriceDiscount())
                    .buyCounts(buyNumber)
                    .build();
            // 插入订单规格子表
            this.orderProductsMapper.insert(orderProduct);
            // 扣减当前商品规格库存
            this.productService.decreaseProductSpecStock(productsSpec.getId(), buyNumber);
        }
        // 创建订单主表
        val order = new Orders()
                .builder()
                .id(orderId)
                .userId(userId)
                .payMethod(payMethod)
                .postAmount(postFee)
                .leftMsg(orderComment)
                .receiverName(userAddress.getReceiver())
                .receiverMobile(userAddress.getMobile())
                .receiverAddress(
                        new StringBuilder()
                                .append(userAddress.getProvince())
                                .append(" ")
                                .append(userAddress.getCity())
                                .append(" ")
                                .append(userAddress.getDistrict())
                                .append(" ")
                                .append(userAddress.getDetail())
                                .toString()
                )
                .isComment(BooleanEnum.FALSE.type)
                .isDelete(BooleanEnum.FALSE.type)
                .createdTime(new Date())
                .updatedTime(new Date())
                .realPayAmount(realFee)
                .totalAmount(totalFee)
                .build();
        // 插入订单主表
        this.ordersMapper.insert(order);
        // 插入订单状态子表
        val orderStatus = new OrderStatus()
                .builder()
                .orderId(orderId)
                .orderStatus(OrderStatusEnum.WAIT_PAY.key)
                .createdTime(new Date())
                .build();
        this.orderStatusMapper.insertSelective(orderStatus);
        // 构建发送到支付中心的数据对象
        val merchantOrderRequestDTO = new MerchantOrderRequestDTO()
                .builder()
                .merchantOrderId(orderId)
                .merchantUserId(userId)
                .amount(realFee + postFee)
                .payMethod(payMethod)
                .build();
        return new OrderResponseDTO()
                .builder()
                .orderId(orderId)
                .merchantOrderRequestDTO(merchantOrderRequestDTO)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        this.orderStatusMapper.updateByPrimaryKeySelective(
                new OrderStatus()
                        .builder()
                        .orderId(orderId)
                        .payTime(new Date())
                        .orderStatus(orderStatus)
                        .build()
        );
    }
}
