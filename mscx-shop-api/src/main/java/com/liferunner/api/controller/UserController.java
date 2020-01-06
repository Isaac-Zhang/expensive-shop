package com.liferunner.api.controller;

import com.alibaba.fastjson.JSON;
import com.liferunner.dto.ShopcartRequestDTO;
import com.liferunner.dto.UserAddressRequestDTO;
import com.liferunner.dto.UserRequestDTO;
import com.liferunner.dto.UserResponseDTO;
import com.liferunner.service.IUserService;
import com.liferunner.utils.CookieTools;
import com.liferunner.utils.JsonResponse;
import com.liferunner.utils.RedisUtils;
import com.liferunner.utils.SecurityTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UserController for : 用户API接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@RestController
@RequestMapping(value = "/users")
@Slf4j
@Api(tags = "用户管理")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "用户详情", notes = "查询用户")
    @ApiIgnore
    @GetMapping("/get/{id}")
    //@GetMapping("/{id}") 如果这里设置位这样，每次请求swagger都会进到这里，是一个bug
    public String getUser(@PathVariable Integer id) {
        return "hello, life.";
    }

    @ApiOperation(value = "创建用户", notes = "用户注册接口")
    @PostMapping("/create")
    public JsonResponse createUser(@RequestBody UserRequestDTO userRequestDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        log.info("======= UserRequestDTO = {}", userRequestDTO);
        try {
            if (StringUtils.isBlank(userRequestDTO.getUsername()))
                return JsonResponse.errorMsg("用户名不能为空");
            if (null != this.userService.findUserByUserName(userRequestDTO.getUsername())) {
                return JsonResponse.errorMsg("用户名已存在！");
            }
            if (StringUtils.isBlank(userRequestDTO.getPassword()) ||
                    StringUtils.isBlank(userRequestDTO.getConfirmPassword()) ||
                    userRequestDTO.getPassword().length() < 8) {
                return JsonResponse.errorMsg("密码为空或长度小于8位");
            }
            if (!userRequestDTO.getPassword().equals(userRequestDTO.getConfirmPassword()))
                return JsonResponse.errorMsg("两次密码不一致！");
            val user = this.userService.createUser(userRequestDTO);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(user, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                        JSON.toJSONString(userResponseDTO), true);

                //获取Redis中数据
                val shopcartFromRedisStr = redisUtils.get(SHOPCART_COOKIE_NAME + ":" + user.getId());
                val shopcartFromCookieStr = CookieTools.getCookieValue(request, SHOPCART_COOKIE_NAME, true);
                syncShopcart(shopcartFromRedisStr, shopcartFromCookieStr, user.getId(), request, response);

                return JsonResponse.ok(userResponseDTO);
            }
        } catch (Exception e) {
            log.error("创建用户失败,{}", userRequestDTO);
        }
        return JsonResponse.errorMsg("创建用户失败");
    }

    @ApiOperation(value = "用户登录", notes = "用户登录接口")
    @PostMapping("/login")
    public JsonResponse userLogin(@RequestBody UserRequestDTO userRequestDTO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            if (StringUtils.isBlank(userRequestDTO.getUsername()))
                return JsonResponse.errorMsg("用户名不能为空");
            if (StringUtils.isBlank(userRequestDTO.getPassword()) ||
                    userRequestDTO.getPassword().length() < 8) {
                return JsonResponse.errorMsg("密码为空或长度小于8位");
            }
            val user = this.userService.userLogin(userRequestDTO);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            BeanUtils.copyProperties(user, userResponseDTO);
            log.info("BeanUtils copy object {}", userResponseDTO);
            if (null != userResponseDTO) {
                // 设置前端存储的cookie信息
                CookieTools.setCookie(request, response, "user",
                        JSON.toJSONString(userResponseDTO), true);

                //获取Redis中数据
                val shopcartFromRedisStr = redisUtils.get(SHOPCART_COOKIE_NAME + ":" + user.getId());
                val shopcartFromCookieStr = CookieTools.getCookieValue(request, SHOPCART_COOKIE_NAME, true);
                syncShopcart(shopcartFromRedisStr, shopcartFromCookieStr, user.getId(), request, response);

                return JsonResponse.ok(userResponseDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户登录失败,{},exception = {}", userRequestDTO, e.getMessage());
        }
        return JsonResponse.errorMsg("用户登录失败");
    }

    @ApiOperation(value = "用户登出", notes = "用户登出", httpMethod = "POST")
    @PostMapping("/logout")
    public JsonResponse userLogout(@RequestParam String uid,
                                   HttpServletRequest request, HttpServletResponse response) {
        // clear front's user cookies
        CookieTools.deleteCookie(request, response, "user");
        // clear user shopcart data
        CookieTools.deleteCookie(request, response, SHOPCART_COOKIE_NAME);
        // return operational result
        return JsonResponse.ok();
    }

    @ApiOperation(value = "用户收货地址", notes = "用户收货地址", httpMethod = "POST")
    @PostMapping("/address/list")
    public JsonResponse addressList(@RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return JsonResponse.errorMsg("用户id为空!");
        }
        val addressList = this.userService.getAddressByUserId(userId);
        return JsonResponse.ok(addressList);
    }

    @ApiOperation(value = "新增用户收货地址", notes = "新增用户收货地址", httpMethod = "POST")
    @PostMapping("/address/add")
    public JsonResponse addAddress(@RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        JsonResponse validateResult = validateUserAddress(userAddressRequestDTO);
        if (validateResult.getStatus() != 200) return validateResult;
        //插入地址信息
        this.userService.addAddress(userAddressRequestDTO);
        return JsonResponse.ok();
    }

    @ApiOperation(value = "更新用户收货地址", notes = "更新用户收货地址", httpMethod = "POST")
    @PostMapping("/address/update")
    public JsonResponse updateAddress(
            @RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        JsonResponse validateResult = validateUserAddress(userAddressRequestDTO);
        if (validateResult.getStatus() != 200) return validateResult;
        //更新地址信息
        this.userService.updateAddress(userAddressRequestDTO);
        return JsonResponse.ok();
    }

    @ApiOperation(value = "删除用户收货地址", notes = "删除用户收货地址", httpMethod = "POST")
    @PostMapping("/address/delete")
    public JsonResponse deleteAddress(
            @RequestParam String userId,
            @RequestParam String addressId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            JsonResponse.errorMsg("");
        }
        //删除地址信息
        this.userService.deleteAddress(userId, addressId);
        return JsonResponse.ok();
    }

    @ApiOperation(value = "设置用户默认收货地址", notes = "设置用户默认收货地址", httpMethod = "POST")
    @PostMapping("/address/setDefault")
    public JsonResponse setDefaultAddress(
            @RequestParam String userId,
            @RequestParam String addressId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            JsonResponse.errorMsg("");
        }
        //更新默认地址信息
        this.userService.updateDefaultAddress(userId, addressId);
        return JsonResponse.ok();
    }

    /**
     * 为用户地址新增和更新校验信息
     *
     * @param userAddressRequestDTO
     * @return
     */
    private JsonResponse validateUserAddress(@RequestBody UserAddressRequestDTO userAddressRequestDTO) {
        if (StringUtils.isBlank(userAddressRequestDTO.getReceiver())) {
            return JsonResponse.errorMsg("收货人不能为空");
        }
        if (userAddressRequestDTO.getReceiver().length() > 12) {
            return JsonResponse.errorMsg("收货人姓名不能太长");
        }
        if (StringUtils.isBlank(userAddressRequestDTO.getMobile())) {
            return JsonResponse.errorMsg("收货人手机号不能为空");
        }
        if (userAddressRequestDTO.getMobile().length() != 11) {
            return JsonResponse.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = SecurityTools.checkMobile(userAddressRequestDTO.getMobile());
        if (!isMobileOk) {
            return JsonResponse.errorMsg("收货人手机号格式不正确");
        }
        String province = userAddressRequestDTO.getProvince();
        String city = userAddressRequestDTO.getCity();
        String district = userAddressRequestDTO.getDistrict();
        String detail = userAddressRequestDTO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return JsonResponse.errorMsg("收货地址信息不能为空");
        }
        return JsonResponse.ok();
    }

    /**
     * 同步Redis和本地Cookie中的数据
     *
     * @param shopcartFromRedisStr
     * @param shopcartFromCookieStr
     * @param request
     * @param response
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
                List<ShopcartRequestDTO> shopcartRedisList = JSON.parseArray(shopcartFromRedisStr, ShopcartRequestDTO.class);
                List<ShopcartRequestDTO> shopcartCookieList = JSON.parseArray(shopcartFromCookieStr, ShopcartRequestDTO.class);

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
                CookieTools.setCookie(request, response, SHOPCART_COOKIE_NAME, JSON.toJSONString(shopcartRedisList), true);

            } else {
                // 直接将Redis数据同步到cookie
                CookieTools.setCookie(request, response, SHOPCART_COOKIE_NAME, shopcartFromRedisStr, true);
            }
        } else if (StringUtils.isNotBlank(shopcartFromCookieStr)) {
            // 直接将cookie中的数据存储到Redis中
            redisUtils.set(SHOPCART_COOKIE_NAME + ":" + uid, shopcartFromCookieStr);
        }
    }
}
