package com.liferunner.api.controller;

import com.liferunner.service.IUserService;
import com.liferunner.utils.JsonResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CertificationController for : 认证接口提供
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/4
 */
@RestController
@RequestMapping(name = "/certification")
public class CertificationController {

    @Autowired
    public IUserService userService;

    @GetMapping("/validateUsername")
    public JsonResponse validateUsername(@RequestParam String username) {
        // 判断用户名是否非法
        if (StringUtils.isBlank(username))
            return JsonResponse.errorMsg("用户名不能为空！");
        if (null != userService.findUserByUserName(username))
            return JsonResponse.errorMsg("用户名已存在！");
        // 用户名可用
        return JsonResponse.ok();
    }
}
