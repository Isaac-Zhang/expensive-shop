package com.liferunner.service.impl;

import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.Users;
import com.liferunner.service.IUserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * UserServiceImpl for : User service 实现
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/4
 */
@Service
public class UserServiceImpl implements IUserService {

    // 构造器注入
    private final UsersMapper usersMapper;
    @Autowired
    public UserServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @Override
    public Users findUserByUserName(String username) {
        // 构建查询条件
        Example example = new Example(Users.class);
        val condition = example.createCriteria()
                .andEqualTo("username", username);
        return this.usersMapper.selectOneByExample(example);
    }
}
