package com.liferunner.service;

import com.liferunner.pojo.Users;

/**
 * IUserService for : TODO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/4
 */
public interface IUserService {

    /**
     * 根据用户名查询用户是否存在
     * @param username
     * @return
     */
    Users findUserByUserName(String username);
}
