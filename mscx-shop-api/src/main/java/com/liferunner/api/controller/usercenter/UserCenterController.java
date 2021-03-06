package com.liferunner.api.controller.usercenter;

import com.alibaba.fastjson.JSON;
import com.liferunner.api.controller.BaseController;
import com.liferunner.dto.UserResponseDTO;
import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.enums.OrderStatusEnum;
import com.liferunner.service.IOrderService;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.CommonPagedResult;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserCenterController for : 用户中心controller
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
@RestController
@Slf4j
@RequestMapping("/usercenter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "用户中心相关API接口", value = "用户中心controller")
public class UserCenterController extends BaseController {

    private final IUserCenterLoginUserService userCenterLoginUserService;
    private final IOrderService orderService;
    private final RedisUtils redisUtils;

    /**
     * @return
     */
    @PostMapping("/userinfo")
    @ApiOperation(notes = "根据用户id获取用户", value = "根据用户id获取用户")
    public JsonResponse findUserByUid(@RequestParam String uid) {
        val user = this.userCenterLoginUserService.findUserById(uid);
        if (null != user) {
            return JsonResponse.ok(user);
        }
        return JsonResponse.errorMsg("获取用户信息失败");
    }

    @PostMapping("/update")
    @ApiOperation(notes = "根据用户id更新用户", value = "根据用户id更新用户")
    public JsonResponse updateUser(
        @RequestParam String uid,
        @RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO,
        BindingResult result,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        if (result.hasErrors()) {
            val errorsMap = getErrorsMap(result);
            return JsonResponse.errorMap(errorsMap);
        }
        log.info("==========update user:{} begin by uid:{}",
            JSON.toJSONString(userUpdateRequestDTO),
            uid);
        val updateUser = this.userCenterLoginUserService.updateUser(uid, userUpdateRequestDTO);
        if (null != updateUser) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(updateUser, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                String userToken = UUID.randomUUID().toString();
                //设置用户token到redis
                redisUtils.set(REDIS_USER_TOKEN + ":" + updateUser.getId(), userToken);
                userResponseDTO.setUserToken(userToken);
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                    JSON.toJSONString(userResponseDTO), true);
                log.info("==========update user:{} success by uid:{}",
                    JSON.toJSONString(userResponseDTO),
                    uid);
                return JsonResponse.ok(userResponseDTO);
            }
        }
        log.warn("==========update user failed:{} by uid:{}",
            JSON.toJSONString(userUpdateRequestDTO),
            uid);
        return JsonResponse.errorMsg("更新用户失败");
    }

    @PostMapping("/upload")
    @ApiOperation(notes = "用户头像上传", value = "用户头像上传")
    public JsonResponse uploadFile(
        @RequestParam String uid,
        MultipartFile file,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        if (null == file) {
            return JsonResponse.errorMsg("文件不能为空");
        }
        String filePathDir = IMG_FACE_UPLOAD_PATH;
        // 每个用户单独存储一个目录
        String uploadPathPrefix = File.separator + uid;
        //获取文件名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return JsonResponse.errorMsg("文件名称不能为空");
        }
        //文件重命名
        String[] fileNameArray = filename.split("\\.");

        //获取文件后缀名
        String fileSuffix = fileNameArray[fileNameArray.length - 1];
        if (!(fileSuffix.equalsIgnoreCase("png") ||
            fileSuffix.equalsIgnoreCase("jpg") ||
            fileSuffix.equalsIgnoreCase("jpeg"))) {
            return JsonResponse.errorMsg("上传图片格式错误！");
        }
        String newFileName = "face-" + uid + "-"
            + System.currentTimeMillis()
            + "." + fileSuffix;
        //文件上传的最终保存位置
        String fileSavePath = filePathDir + uploadPathPrefix + File.separator + newFileName;
        String faceWebUrl = IMG_FACE_BASE_WEB_URL + "/face-img/" + uid + "/" + newFileName;
        File fileOut = new File(fileSavePath);
        if (fileOut.getParentFile() != null) {
            //创建存储文件夹
            fileOut.getParentFile().mkdirs();
            //输出文件到保存目录
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(fileOut);
                InputStream inputStream = file.getInputStream();
                IOUtils.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        val userFace = this.userCenterLoginUserService.updateUserFace(uid, faceWebUrl);
        if (null != userFace) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(userFace, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                String userToken = UUID.randomUUID().toString();
                //设置用户token到redis
                redisUtils.set(REDIS_USER_TOKEN + ":" + userFace.getId(), userToken);
                userResponseDTO.setUserToken(userToken);
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                    JSON.toJSONString(userResponseDTO), true);
                log.info("==========update user:{} success by uid:{}",
                    JSON.toJSONString(userResponseDTO),
                    uid);
                return JsonResponse.ok(userResponseDTO);
            }
        }
        return JsonResponse.ok();
    }

    @PostMapping("/userorders")
    @ApiOperation(notes = "查询用户订单", value = "查询用户订单")
    public JsonResponse getUserOrderList(
        @RequestParam String userId,
        @RequestParam Integer orderStatus,
        @RequestParam Integer pageNumber,
        @RequestParam Integer pageSize) {
        val userOrderList = this.userCenterLoginUserService.getUserOrderList(userId, orderStatus, pageNumber, pageSize);
        return JsonResponse.ok(userOrderList);
    }

    @PostMapping("/marchantDeliverGoods")
    @ApiOperation(notes = "模拟商户发货", value = "模拟订单发货")
    public JsonResponse marchantDeliverGoods(@RequestParam String orderId) {
        if (StringUtils.isBlank(orderId)) {
            log.warn("发货订单号码不正确！");
            return JsonResponse.errorMsg("发货订单号码不正确");
        }
        //todo：更新订单之前，订单状态必须是已付款待发货

        //更新订单状态信息 从付款未发货-->已发货待查收
        val result = this.userCenterLoginUserService
            .updateDeliverOrderStatus(orderId, OrderStatusEnum.WAIT_RECEIVE.key);
        if (result) {
            return JsonResponse.ok();
        }
        log.warn("订单:{} 发货更新状态失败！", orderId);
        return JsonResponse.errorMsg("发货更新状态失败！");
    }

    @PostMapping("confirmReceive")
    @ApiOperation(notes = "用户确认收货", value = "用户确认收货")
    public JsonResponse confirmReceive(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "orderId", value = "订单id", required = true)
        @RequestParam String orderId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return JsonResponse.errorMsg("用户确认订单参数错误");
        }

        //验证订单和用户关系
        val relationship = validateRelationshipUserAndOrder(userId, orderId);
        if (relationship) {
            //验证通过之后，更新用户订单状态
            val result = this.userCenterLoginUserService.updateReceiveOrderStatus(orderId, OrderStatusEnum.SUCCESS.key);
            return JsonResponse.ok("收货完成");
        }

        return JsonResponse.errorMsg("确认收货失败！");
    }

    @PostMapping("/delete")
    @ApiOperation(notes = "用户删除订单", value = "用户删除订单")
    public JsonResponse deleteOrder(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "orderId", value = "订单id", required = true)
        @RequestParam String orderId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(orderId)) {
            return JsonResponse.errorMsg("用户确认订单参数错误");
        }

        //验证订单和用户关系
        val relationship = validateRelationshipUserAndOrder(userId, orderId);
        if (relationship) {
            // 逻辑删除订单
            val result = this.orderService.deleteOrder(orderId);
            return result > 0 ? JsonResponse.errorMsg("订单删除成功！")
                : JsonResponse.errorMsg("订单删除失败！");
        }

        return JsonResponse.errorMsg("订单删除失败！");
    }

    @PostMapping("/countOrderStatus")
    @ApiOperation(notes = "统计订单状态流转数", value = "统计订单状态流转数")
    public JsonResponse countOrderStatus(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId
    ) {
        if (StringUtils.isBlank(userId)) {
            return JsonResponse.errorMsg("参数错误");
        }
        val userCenterCounterResponseDTO = this.orderService.countOrderByStatus(userId);
        return JsonResponse.ok(userCenterCounterResponseDTO);
    }

    @ApiOperation(value = "查询订单日志动向", notes = "查询订单日志动向", httpMethod = "POST")
    @PostMapping("/trend")
    public JsonResponse trend(
        @ApiParam(name = "userId", value = "用户id", required = true)
        @RequestParam String userId,
        @ApiParam(name = "pageNumber", value = "查询下一页的第几页", required = false)
        @RequestParam Integer pageNumber,
        @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
        @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return JsonResponse.errorMsg(null);
        }
        if (null == pageNumber || 0 == pageNumber) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if (null == pageSize || 0 == pageSize) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        CommonPagedResult commonPagedResult = this.userCenterLoginUserService.getOrdersTrend(userId,
            pageNumber,
            pageSize);

        return JsonResponse.ok(commonPagedResult);
    }

}
