package com.liferunner.service.impl;

import com.liferunner.dto.UserRequestDTO;
import com.liferunner.enums.SexEnum;
import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.Users;
import com.liferunner.service.IUserService;
import com.liferunner.utils.MD5GeneratorTools;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * UserServiceImpl for : User service 实现
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/4
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private final String FACE_IMG = "https://avatars1.githubusercontent.com/u/4083152?s=88&v=4";

    // 构造器注入
    private final UsersMapper usersMapper;
    private final Sid sid;

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, Sid sid) {
        this.usersMapper = usersMapper;
        this.sid = sid;
    }

    @Override
    public Users findUserByUserName(String username) {
        // 构建查询条件
        Example example = new Example(Users.class);
        val condition = example.createCriteria()
                .andEqualTo("username", username);
        return this.usersMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserRequestDTO userRequestDTO) throws Exception {
        log.info("======begin create user : {}=======", userRequestDTO);
        val user = Users.builder()
                .id(sid.next()) //生成分布式id
                .username(userRequestDTO.getUsername())
                .password(MD5GeneratorTools.getMD5Str(userRequestDTO.getPassword()))
                .birthday(DateUtils.parseDate("1970-01-01", "yyyy-MM-dd"))
                .nickname(userRequestDTO.getUsername())
                .face(this.FACE_IMG)
                .sex(SexEnum.secret.type)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        this.usersMapper.insertSelective(user);
        log.info("======end create user : {}=======", userRequestDTO);
        return user;
    }
}
