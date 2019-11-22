package com.liferunner.api.controller.usercenter;

import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class UserCenterController {
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
}
