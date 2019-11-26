package com.liferunner.service.impl.usercenter;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liferunner.custom.OrderCustomMapper;
import com.liferunner.dto.UserOrderResponseDTO;
import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.mapper.OrderStatusMapper;
import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.OrderStatus;
import com.liferunner.pojo.Users;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.CommonPagedResult;
import com.liferunner.utils.SecurityTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserCenterLoginUserServiceImpl for : 实现用户中心登录用户相关操作service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCenterLoginUserServiceImpl implements IUserCenterLoginUserService {

    private final UsersMapper usersMapper;
    private final OrderCustomMapper orderCustomMapper;
    private final OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users findUserById(String uid) {
        Users user = this.usersMapper.selectByPrimaryKey(uid);
        user.setPassword(SecurityTools.HiddenPartString4SecurityDisplay(user.getPassword()));
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUser(String uid, UserUpdateRequestDTO userUpdateRequestDTO) {
        Users user = new Users();
        BeanUtils.copyProperties(userUpdateRequestDTO, user);
        user.setId(uid);
        user.setUpdatedTime(new Date());
        var userResult = this.usersMapper.updateByPrimaryKeySelective(user);
        return this.usersMapper.selectByPrimaryKey(uid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String uid, String faceUrl) {
        Users user = new Users();
        user.setId(uid);
        user.setFace(faceUrl);
        user.setUpdatedTime(new Date());
        var userResult = this.usersMapper.updateByPrimaryKeySelective(user);
        return this.usersMapper.selectByPrimaryKey(uid);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommonPagedResult getUserOrderList(String uid, Integer orderStatus, Integer pageNumber, Integer pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", uid);
        if (orderStatus != null) {
            paramMap.put("orderStatus", orderStatus);
        }
        // mybatis-pagehelper
        PageHelper.startPage(pageNumber, pageSize);

        val userOrderList = this.orderCustomMapper.getUserOrderList(paramMap);
        // 获取mybatis插件中获取到信息
        PageInfo<?> pageInfo = new PageInfo<>(userOrderList);
        // 封装为返回到前端分页组件可识别的视图
        return CommonPagedResult.builder()
            .pageNumber(pageNumber)
            .rows(userOrderList)
            .totalPage(pageInfo.getPages())
            .records(pageInfo.getTotal())
            .build();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updateDeliverOrderStatus(String orderId, Integer orderStatus) {
        val result = this.orderStatusMapper.updateByPrimaryKeySelective(
            OrderStatus.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .deliverTime(new Date())
                .build()
        );
        return result > 0 ? true : false;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean updateReceiveOrderStatus(String orderId, Integer orderStatus) {
        val result = this.orderStatusMapper.updateByPrimaryKeySelective(
            OrderStatus.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .successTime(new Date())
                .build()
        );
        return result > 0 ? true : false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommonPagedResult getOrdersTrend(String userId, Integer pageNumber, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        PageHelper.startPage(pageNumber, pageSize);
        List<OrderStatus> list = this.orderCustomMapper.getMyOrderJournal(map);
        PageInfo<?> pageInfo = new PageInfo<>(list);
        return CommonPagedResult.builder()
            .pageNumber(pageNumber)
            .rows(list)
            .totalPage(pageInfo.getPages())
            .records(pageInfo.getTotal())
            .build();
    }
}
