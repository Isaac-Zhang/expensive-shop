package com.liferunner.api.controller;

import com.liferunner.dto.UserRequestDTO;
import com.liferunner.service.IUserService;
import com.liferunner.utils.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * UserController for : 用户API接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@RestController
@RequestMapping(name = "/users")
@Slf4j
@Api(tags="用户管理")
public class UserController {

    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户详情",notes = "查询用户")
    @ApiIgnore
    @GetMapping("/get/{id}")
    //@GetMapping("/{id}") 如果这里设置位这样，每次请求swagger都会进到这里，是一个bug
    public String getUser(@PathVariable Integer id) {
        return "hello, life.";
    }

    @ApiOperation(value = "创建用户",notes = "用户注册接口")
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
