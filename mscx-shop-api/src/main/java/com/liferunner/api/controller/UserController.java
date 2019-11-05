package com.liferunner.api.controller;

import com.liferunner.dto.UserRequestDTO;
import com.liferunner.service.IUserService;
import com.liferunner.utils.JsonResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
            val user = this.userService.createUser(userRequestDTO);
            if (null != user)
                return JsonResponse.ok(user);
        } catch (Exception e) {
            log.error("创建用户失败,{}", userRequestDTO);
        }
        return JsonResponse.errorMsg("创建用户失败");
    }
}
