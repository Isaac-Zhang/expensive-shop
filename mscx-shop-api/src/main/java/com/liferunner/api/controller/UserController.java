package com.liferunner.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController for : 用户API接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@RestController
@RequestMapping(name = "/users")
public class UserController {

    @GetMapping("/{id}")
    public String getUser(@PathVariable Integer id){
        return "hello, life.";
    }
}
