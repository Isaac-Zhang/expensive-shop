package com.liferunner.sso.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.ShopcartRequestDTO;
import com.liferunner.dto.UserRequestDTO;
import com.liferunner.dto.UserResponseDTO;
import com.liferunner.service.IUserService;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.MD5GeneratorTools;
import com.liferunner.utils.RedisUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SsoController for TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/4/14
 **/
@Slf4j
@Controller
@RequestMapping(path = "/sso")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SsoController {

    private final RedisUtils redisUtils;
    private final IUserService userService;
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";
    public static final String SHOPCART_COOKIE_NAME = "shopcart";

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response,
        String returnUrl) {
        model.addAttribute("returnUrl", returnUrl);
        // 返回到login 页面
        return "login";
    }

    /**
     * 在SSO登录成功之后，需要执行： 1. 创建全局token 2. 将全局token 写入 redis & 写入当前域 下的cookie中 3. 创建临时token 4. 将临时token 写入 redis 5.
     * 将临时token 跟随 returnUrl 回传到前端
     */
    @PostMapping("/login")
    public String login(Model model, HttpServletRequest request, HttpServletResponse response,
        String returnUrl, String username, String password) {
        try {
            if (StringUtils.isBlank(username)) {
                model.addAttribute("errorMsg", "用户名不能为空");
                return "login";
            }
            if (StringUtils.isBlank(password) || password.length() < 8) {
                model.addAttribute("errorMsg", "密码为空或长度小于8位");
                return "login";
            }
            val user = this.userService
                .userLogin(UserRequestDTO.builder().username(username).password(password).build());
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != user) {
                BeanUtils.copyProperties(user, userResponseDTO);
                String userToken = UUID.randomUUID().toString();
                // 设置用户token到redis
                redisUtils.set(REDIS_USER_TOKEN + ":" + user.getId(), userToken);
                userResponseDTO.setUserToken(userToken);
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                    JSON.toJSONString(userResponseDTO), true);

                //获取Redis中数据
                val shopcartFromRedisStr = redisUtils.get(SHOPCART_COOKIE_NAME + ":" + user.getId());
                val shopcartFromCookieStr = CookieTools.getCookieValue(request, SHOPCART_COOKIE_NAME, true);
                syncShopcart(shopcartFromRedisStr, shopcartFromCookieStr, user.getId(), request, response);

                // 登录成功，开始处理
                //      1. 创建全局ticket,表示已经在SSO 登录成功
                String user_ticket = UUID.randomUUID().toString().trim();
                //      2. 将全局ticket 写入 redis & 写入当前域 下的cookie中
                redisUtils.set(REDIS_USER_TICKET + ":" + user_ticket, user.getId());
                setUserTicketToCookie(COOKIE_USER_TICKET, user_ticket, response);
                //      3. 创建临时ticket
                String user_tmp_ticket = UUID.randomUUID().toString().trim();
                //      4. 将临时ticket 写入 redis,有效期10分钟
                redisUtils.set(REDIS_TMP_TICKET + ":" + user_tmp_ticket, MD5GeneratorTools.getMD5Str(user_tmp_ticket)
                    , 600);
                //      5. 将临时ticket 跟随 returnUrl 回传到前端
                 return "redirect:"+returnUrl+"?tmpTicket="+user_tmp_ticket;
                //return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户登录失败,{},exception = {}", username, e.getMessage());
        }
        return "login";
    }

    /**
     * 根据临时票据获取用户会话信息 1. 验证临时票据是否有效 2. 如果有效，删除临时票据 3. 根据cookie中的全局会话，获取用户全局会话ticket 4. 根据全局会话ticket 获取用户id 5. 根据用户id
     * ,获取redis中的全局会话内容
     */
    @PostMapping("verifyTmpTicket")
    @ResponseBody
    public JsonResponse verifyTmpTicket(String tmpTicket, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        // 1. 验证临时票据是否有效
        if (StringUtils.isBlank(tmpTicket)) {
            return JsonResponse.errorMsg("用户未登录");
        }
        String userTmpTicket = redisUtils.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(userTmpTicket) || !userTmpTicket.equals(MD5GeneratorTools.getMD5Str(tmpTicket))) {
            return JsonResponse.errorMsg("用户未登录");
        }
        // 2. 如果有效，删除临时票据
        redisUtils.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        // 3. 根据cookie中的全局会话，获取用户全局会话ticket
        String userTicket =
            Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName() == COOKIE_USER_TICKET).findFirst()
                .toString();
        if (StringUtils.isBlank(userTicket)) {
            return JsonResponse.errorMsg("用户未登录");
        }
        // 4. 根据全局会话ticket 获取用户id
        String redisUserId = redisUtils.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(redisUserId)) {
            return JsonResponse.errorMsg("用户已退出，请重新登录。");
        }
        // 5. 根据用户id ,获取redis中的全局会话内容
        String redisUserInfo = redisUtils.get(REDIS_USER_TOKEN + ":" + redisUserId);
        if (StringUtils.isBlank(redisUserId)) {
            return JsonResponse.errorMsg("用户已退出，请重新登录。");
        }
        UserResponseDTO userResponseDTO = JSON.parseObject(redisUserInfo, UserResponseDTO.class);
        return JsonResponse.ok(userResponseDTO);
    }

    /**
     * 同步Redis和本地Cookie中的数据
     */
    private void syncShopcart(String shopcartFromRedisStr,
        String shopcartFromCookieStr,
        String uid,
        HttpServletRequest request,
        HttpServletResponse response) {
        // 1.如果Redis为空，Cookie数据为空，不做处理
        // 2.如果Redis为空，Cookie数据不为空，将Cookie数据同步存储到Redis中
        // 3.如果Redis不为空，Cookie数据为空，将Redis数据同步到Cookie中
        // 4.如果Redis不为空，Cookie数据不为空，以Redis数据为主数据，合并数据（Cookie数据在Redis中已存在，以本地Cookie数据为主）

        if (StringUtils.isNotBlank(shopcartFromRedisStr)) {
            if (StringUtils.isNotBlank(shopcartFromCookieStr)) {
                List<ShopcartRequestDTO> shopcartRedisList = JSON
                    .parseArray(shopcartFromRedisStr, ShopcartRequestDTO.class);
                List<ShopcartRequestDTO> shopcartCookieList = JSON
                    .parseArray(shopcartFromCookieStr, ShopcartRequestDTO.class);

                //循环redis中的数据
                List<ShopcartRequestDTO> peddingRemovedList = new ArrayList<>();

                shopcartRedisList.forEach(ri -> {
                    shopcartCookieList.forEach(ci -> {
                        // 判断redis中和cookie中是否存在相同的商品，如果存在，
                        // 则标记要从cookie中删除的list,使用cookie中的数量覆盖redis数量
                        if (ri.getSpecId().equals(ci.getSpecId())) {
                            peddingRemovedList.add(ci);
                            ri.setBuyCounts(ci.getBuyCounts());
                        }
                    });
                });

                // 首先删除掉cookie中标记要删除的数据
                shopcartCookieList.removeAll(peddingRemovedList);
                // 将两个list数据合并
                shopcartRedisList.addAll(shopcartCookieList);

                redisUtils.set(SHOPCART_COOKIE_NAME + ":" + uid, JSON.toJSONString(shopcartRedisList));
                CookieTools
                    .setCookie(request, response, SHOPCART_COOKIE_NAME, JSON.toJSONString(shopcartRedisList), true);

            } else {
                // 直接将Redis数据同步到cookie
                CookieTools.setCookie(request, response, SHOPCART_COOKIE_NAME, shopcartFromRedisStr, true);
            }
        } else if (StringUtils.isNotBlank(shopcartFromCookieStr)) {
            // 直接将cookie中的数据存储到Redis中
            redisUtils.set(SHOPCART_COOKIE_NAME + ":" + uid, shopcartFromCookieStr);
        }
    }

    /**
     * 设置信息到前端sso cookie
     */
    private void setUserTicketToCookie(String key, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
//        CookieTools.setCookie(request, response, COOKIE_USER_TICKET, userTicket, true);
    }
}
