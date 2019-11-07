package com.liferunner.service;

import com.liferunner.dto.UserRequestDTO;
import com.liferunner.pojo.Users;

/**
 * IUserService for : 用户信息接口
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/4
 */
public interface IUserService {

    /**
     * 根据用户名查询用户是否存在
     *
     * @param username 用户名
     * @return {@link Users}
     */
    Users findUserByUserName(String username);

    /**
     * 创建用户
     *
     * @param userRequestDTO 用户请求dto
     * @return 当前用户
     */
    Users createUser(UserRequestDTO userRequestDTO) throws Exception;

    /**
     * 用户登录
     * @param userRequestDTO 请求dto
     * @return 登录用户信息
     * @throws Exception
     */
    Users userLogin(UserRequestDTO userRequestDTO) throws Exception;
}
