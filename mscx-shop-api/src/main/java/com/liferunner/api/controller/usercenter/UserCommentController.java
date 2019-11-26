package com.liferunner.api.controller.usercenter;

import com.liferunner.api.controller.BaseController;
import com.liferunner.dto.UserCommentRequestDTO;
import com.liferunner.service.usercenter.IUserCommentService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/***
 * 用户评价controller
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/11/26
 */
@RestController
@Slf4j
@RequestMapping("/usercenter/comment")
@Api(tags = "用户评论", value = "用户评价相关API接口")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCommentController extends BaseController {

    private final IUserCommentService userCommentService;

    @PostMapping("/pendingComment")
    @ApiOperation(notes = "查询待评价订单", value = "查询待评价订单")
    public JsonResponse pendingCommentOrders(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "orderId", value = "订单id", required = true)
        @RequestParam String orderId
    ) {
        // 校验用户与订单相匹配
        val validateResult = validateRelationshipUserAndOrder(userId, orderId);
        if (!validateResult) {
            log.warn("评价用户与订单不匹配，请检查:{}-{}", userId, orderId);
            return JsonResponse.errorMsg("评价错误！");
        }
        //TODO: 校验是否已经评价

        val userPendingComments = this.userCommentService.getUserPendingComments(orderId);
        return JsonResponse.ok(userPendingComments);
    }

    @PostMapping("/saveUserComments")
    @ApiOperation(notes = "保存评价", value = "保存评价")
    public JsonResponse saveUserComments(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "orderId", value = "订单id", required = true)
        @RequestParam String orderId,
        @RequestBody List<UserCommentRequestDTO> userCommentRequestDTOList
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)
            || CollectionUtils.isEmpty(userCommentRequestDTOList)) {
            return JsonResponse.errorMsg("用户评价参数错误");
        }
        // 校验用户与订单相匹配
        val validateResult = validateRelationshipUserAndOrder(userId, orderId);
        if (!validateResult) {
            log.warn("评价用户与订单不匹配，请检查:{}-{}", userId, orderId);
            return JsonResponse.errorMsg("评价错误！");
        }

        val result = this.userCommentService.saveUserComments(orderId, userId, userCommentRequestDTOList);
        return result > 0 ? JsonResponse.ok("评价保存成功") : JsonResponse.errorMsg("评价保存失败");
    }

    @PostMapping("/mycomments")
    @ApiOperation(value = "查询我的评价", notes = "查询我的评价")
    public JsonResponse getProductComment(
        @ApiParam(name = "userId", value = "商品id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "pageNumber", value = "当前页码", required = false, example = "1")
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "每页展示记录数", required = false, example = "10")
        @RequestParam Integer pageSize
    ) {
        if (StringUtils.isBlank(userId)) {
            return JsonResponse.errorMsg("用户id不能为空！");
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        log.info("============查询我的评价:{}==============", userId);

        val userCommentList = this.userCommentService.getUserCommentList(userId, pageNumber, pageSize);

        return JsonResponse.ok(userCommentList);
    }
}
