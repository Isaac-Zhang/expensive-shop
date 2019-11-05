package com.liferunner.api.controller;

import com.liferunner.dto.UserRequestDTO;
import com.liferunner.service.IUserService;
import com.liferunner.utils.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * UserController for : 用户API接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@RestController
@RequestMapping(name = "/users")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    public String getUser(@PathVariable Integer id) {
        return "hello, life.";
    }

    @PostMapping("/create")
    public JsonResponse createUser(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            if (StringUtils.isBlank(userRequestDTO.getUsername()))
                return JsonResponse.errorMsg("用户名不能为空");
            if (null != this.userService.findUserByUserName(userRequestDTO.getUsername())) {
                return JsonResponse.errorMsg("用户名已存在！");
            }
            if (StringUtils.isBlank(userRequestDTO.getPassword()) ||
                    StringUtils.isBlank(userRequestDTO.getConfimPassword()) ||
                    userRequestDTO.getPassword().length() < 8) {
                return JsonResponse.errorMsg("密码为空或长度小于8位");
            }
            if (!userRequestDTO.getPassword().equals(userRequestDTO.getConfimPassword()))
                return JsonResponse.errorMsg("两次密码不一致！");
            val user = this.userService.createUser(userRequestDTO);
            if (null != user)
                return JsonResponse.ok(user);
        } catch (Exception e) {
            log.error("创建用户失败,{}", userRequestDTO);
        }
        return JsonResponse.errorMsg("创建用户失败");
    }
}
