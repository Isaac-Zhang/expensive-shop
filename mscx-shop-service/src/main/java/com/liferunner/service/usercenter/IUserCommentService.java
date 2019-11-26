package com.liferunner.service.usercenter;

import com.liferunner.dto.UserCommentRequestDTO;
import com.liferunner.pojo.OrderProducts;
import com.liferunner.pojo.ProductsComments;
import com.liferunner.utils.CommonPagedResult;
import java.util.List;

/***
 * 用户评价service
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/11/26
 */
public interface IUserCommentService {

    /***
     * 查询等待评价的订单商品
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param orderId
     * @return java.util.List<com.liferunner.pojo.OrderProducts>
     * @throws
     */
    List<OrderProducts> getUserPendingComments(String orderId);

    /***
     * 用户评价
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param orderId
     * @param userId
     * @param commentList
     * @return int
     * @throws
     */
    int saveUserComments(String orderId, String userId, List<UserCommentRequestDTO> commentList);

    /**
     * 根据用户id查询用户评价信息
     *
     * @param uid
     * @return
     */
    CommonPagedResult getUserCommentList(String uid, Integer pageNumber, Integer pageSize);

}
