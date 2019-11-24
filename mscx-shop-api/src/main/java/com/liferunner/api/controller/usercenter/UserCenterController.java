package com.liferunner.api.controller.usercenter;

import com.alibaba.fastjson.JSON;
import com.liferunner.api.controller.BaseController;
import com.liferunner.dto.UserResponseDTO;
import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

    /**
     * @return
     */
    @PostMapping("/userinfo")
    @ApiOperation(tags = "根据用户id获取用户", value = "根据用户id获取用户")
    public JsonResponse findUserByUid(@RequestParam String uid) {
        val user = this.userCenterLoginUserService.findUserById(uid);
        if (null != user) {
            return JsonResponse.ok(user);
        }
        return JsonResponse.errorMsg("获取用户信息失败");
    }

    @PostMapping("/update")
    @ApiOperation(tags = "根据用户id更新用户", value = "根据用户id更新用户")
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
}
