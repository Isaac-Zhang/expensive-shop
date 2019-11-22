package com.liferunner.service.impl.usercenter;

import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.Users;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.SecurityTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * UserCenterLoginUserServiceImpl for : 实现用户中心登录用户相关操作service
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/22
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCenterLoginUserServiceImpl implements IUserCenterLoginUserService {

    private final UsersMapper usersMapper;

    @Override
    public Users findUserById(String uid) {
        Users user = this.usersMapper.selectByPrimaryKey(uid);
        user.setPassword(SecurityTools.HiddenPartString4SecurityDisplay(user.getPassword()));
        return user;
    }
}
