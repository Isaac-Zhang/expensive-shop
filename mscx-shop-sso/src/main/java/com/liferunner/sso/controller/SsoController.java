package com.liferunner.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SsoController for TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/4/14
 **/
@Controller
@RequestMapping(path = "/sso")
public class SsoController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response,
        String returnUrl) {

        model.addAttribute("returnUrl", returnUrl);
        // 返回到login 页面
        return "login";
    }

}
