package com.liferunner.service.impl.usercenter;

import com.liferunner.dto.UserUpdateRequestDTO;
import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.Users;
import com.liferunner.service.usercenter.IUserCenterLoginUserService;
import com.liferunner.utils.SecurityTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    @Override
    public Users updateUser(String uid, UserUpdateRequestDTO userUpdateRequestDTO) {
        Users user = new Users();
        BeanUtils.copyProperties(userUpdateRequestDTO, user);
        user.setId(uid);
        user.setUpdatedTime(new Date());
        var userResult = this.usersMapper.updateByPrimaryKeySelective(user);
        return this.usersMapper.selectByPrimaryKey(uid);
    }
}
