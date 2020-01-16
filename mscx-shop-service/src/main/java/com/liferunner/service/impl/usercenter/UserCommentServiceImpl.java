package com.liferunner.service.impl.usercenter;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liferunner.custom.CommentCustomMapper;
import com.liferunner.dto.UserCommentRequestDTO;
import com.liferunner.enums.BooleanEnum;
import com.liferunner.mapper.OrderProductsMapper;
import com.liferunner.mapper.OrderStatusMapper;
import com.liferunner.mapper.OrdersMapper;
import com.liferunner.pojo.OrderProducts;
import com.liferunner.pojo.OrderStatus;
import com.liferunner.pojo.Orders;
import com.liferunner.service.usercenter.IUserCommentService;
import com.liferunner.utils.CommonPagedResult;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***
 * 实现用户评价service
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/11/26
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCommentServiceImpl implements IUserCommentService {

    private final OrderProductsMapper orderProductsMapper;
    private final OrdersMapper ordersMapper;
    private final CommentCustomMapper commentCustomMapper;
    private final OrderStatusMapper orderStatusMapper;
    private final Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<OrderProducts> getUserPendingComments(String orderId) {
        return this.orderProductsMapper.select(
            OrderProducts.builder().orderId(orderId).build()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int saveUserComments(String orderId, String userId, List<UserCommentRequestDTO> commentList) {
        int result = 0;

        // 1. 保存评价 items_comments
        // 1.1 循环赋id
        commentList.stream().forEach(i -> {
            i.setCommentId(sid.nextShort());
        });
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("commentList", commentList);
        result = this.commentCustomMapper.saveUserComments(paramMap);

        // 2. 更新订单为已评价
        result = this.ordersMapper.updateByPrimaryKeySelective(
            Orders.builder().id(orderId).isComment(BooleanEnum.TRUE.type).build()
        );
        // 3. 更新订单状态表中的留言时间
        result = this.orderStatusMapper.updateByPrimaryKeySelective(
            OrderStatus.builder().orderId(orderId).commentTime(new Date()).build()
        );

        return result;
    }

    @Override
    public CommonPagedResult getUserCommentList(String uid, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", uid);
        PageHelper.startPage(pageNumber, pageSize);
        val userCommentList = this.commentCustomMapper.getUserCommentList(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(userCommentList);
        // 封装为返回到前端分页组件可识别的视图
        val commonPagedResult = CommonPagedResult.builder()
            .pageNumber(pageNumber)
            .rows(userCommentList)
            .totalPage(pageInfo.getPages())
            .records(pageInfo.getTotal())
            .build();
        return commonPagedResult;
    }
}
