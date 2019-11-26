package com.liferunner.service.usercenter;

import com.liferunner.dto.UserOrderResponseDTO;
import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.pojo.Users;
import com.liferunner.utils.CommonPagedResult;

import java.util.List;

/**
 * IUserCenterLoginUserService for : 实现用户中心登录用户的相关service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
public interface IUserCenterLoginUserService {

    /**
     * 根据用户id获取用户
     *
     * @param uid
     * @return
     */
    Users findUserById(String uid);

    /**
     * 更新用户信息
     *
     * @param uid
     * @param userUpdateRequestDTO
     * @return
     */
    Users updateUser(String uid, UserUpdateRequestDTO userUpdateRequestDTO);

    /**
     * 更新用户头像
     *
     * @param uid
     * @param faceUrl
     * @return
     */
    Users updateUserFace(String uid, String faceUrl);

    /**
     * 根据用户id查询用户订单
     *
     * @param uid
     * @param orderStatus
     * @return
     */
    CommonPagedResult getUserOrderList(String uid, Integer orderStatus, Integer pageNumber, Integer pageSize);

    /***
     * 更新订单发货
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param orderId
     * @param orderStatus
     * @return boolean
     * @throws
     */
    boolean updateDeliverOrderStatus(String orderId, Integer orderStatus);

    /***
     * 更新订单收货
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param orderId
     * @param orderStatus
     * @return boolean
     * @throws
     */
    boolean updateReceiveOrderStatus(String orderId, Integer orderStatus);

    /***
     * 获取用户订单日志
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @return com.liferunner.utils.CommonPagedResult
     * @throws
     */
    CommonPagedResult getOrdersTrend(String userId, Integer pageNumber, Integer pageSize);
}
