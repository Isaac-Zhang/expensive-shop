package com.liferunner.service.usercenter;

import com.liferunner.pojo.Users;

/**
 * IUserCenterLoginUserService for : 实现用户中心登录用户的相关service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
public interface IUserCenterLoginUserService {

    /**
     * 根据用户id获取用户
     *
     * @param uid
     * @return
     */
    Users findUserById(String uid);
}
