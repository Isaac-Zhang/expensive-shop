package com.liferunner.service.impl;

import com.liferunner.custom.OrderCustomMapper;
import com.liferunner.dto.MerchantOrderRequestDTO;
import com.liferunner.dto.OrderRequestDTO;
import com.liferunner.dto.OrderResponseDTO;
import com.liferunner.dto.ShopcartRequestDTO;
import com.liferunner.dto.UserCenterCounterResponseDTO;
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
import com.liferunner.utils.DateTools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
    private final OrderCustomMapper orderCustomMapper;
    private final OrderProductsMapper orderProductsMapper;
    private final Sid sid;
    private final IUserService userService;
    private final IProductService productService;
    private final OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderResponseDTO createOrder(List<ShopcartRequestDTO> shopcartRequestDTOList,
        OrderRequestDTO orderRequestDTO) {
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
            // 从Redis中获取实际SKU购买数量
            ShopcartRequestDTO dto =
                shopcartRequestDTOList.stream()
                    .filter(i -> i.getSpecId().equals(productsSpec.getId()))
                    .findFirst()
                    .orElseGet(null);
            if (log.isWarnEnabled()) {
                log.warn("{} ----- {}中创建订单时候存在不匹配的数据。", shopcartRequestDTOList, productSpecIds);
            }
            Integer buyNumber = dto == null ? 0 : dto.getBuyCounts();
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
            val orderProduct = OrderProducts
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
        val order = Orders
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
        val orderStatus = OrderStatus
            .builder()
            .orderId(orderId)
            .orderStatus(OrderStatusEnum.WAIT_PAY.key)
            .createdTime(new Date())
            .build();
        this.orderStatusMapper.insertSelective(orderStatus);
        // 构建发送到支付中心的数据对象
        val merchantOrderRequestDTO = MerchantOrderRequestDTO
            .builder()
            .merchantOrderId(orderId)
            .merchantUserId(userId)
            .amount(realFee + postFee)
            .payMethod(payMethod)
            .build();
        return OrderResponseDTO
            .builder()
            .orderId(orderId)
            .merchantOrderRequestDTO(merchantOrderRequestDTO)
            .build();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        this.orderStatusMapper.updateByPrimaryKeySelective(
            OrderStatus
                .builder()
                .orderId(orderId)
                .payTime(new Date())
                .orderStatus(orderStatus)
                .build()
        );
    }

    @Override
    public OrderStatus getPaidResult(String orderId) {
        return this.orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void autoCloseOvertimeOrder() {
        Example example = new Example(OrderStatus.class);
        val condition = example.createCriteria();
        condition.andEqualTo("orderStatus", OrderStatusEnum.WAIT_PAY.key);
        val orderStatusList = this.orderStatusMapper.selectByExample(example);
        //获取到未支付订单
        for (OrderStatus item : orderStatusList) {
            val between = DateTools.daysBetween(item.getCreatedTime(), new Date());
            if (between > 0) {
                closeOrder(item.getOrderId());
            }
        }
    }

    @Override
    public Orders getOrderById(String orderId) {
        return this.ordersMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public int deleteOrder(String orderId) {
        return this.ordersMapper.updateByPrimaryKeySelective(
            Orders.builder().id(orderId).isDelete(BooleanEnum.TRUE.type).build()
        );
    }

    @Override
    public UserCenterCounterResponseDTO countOrderByStatus(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.key);
        int waitPayCounts = this.orderCustomMapper.CountOrderByStatus(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.key);
        int waitDeliverCounts = this.orderCustomMapper.CountOrderByStatus(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.key);
        int waitReceiveCounts = this.orderCustomMapper.CountOrderByStatus(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.key);
        map.put("isComment", BooleanEnum.FALSE.type);
        int waitCommentCounts = this.orderCustomMapper.CountOrderByStatus(map);

        return UserCenterCounterResponseDTO.builder()
            .waitPayCounts(waitPayCounts)
            .waitDeliverCounts(waitDeliverCounts)
            .waitReceiveCounts(waitReceiveCounts)
            .waitCommentCounts(waitCommentCounts)
            .build();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    /**
     * 根据orderId关闭订单
     */
    protected void closeOrder(String orderId) {
        this.orderStatusMapper.updateByPrimaryKeySelective(
            OrderStatus.builder()
                .orderStatus(OrderStatusEnum.CLOSE.key)
                .closeTime(new Date())
                .orderId(orderId)
                .build()
        );
    }
}
