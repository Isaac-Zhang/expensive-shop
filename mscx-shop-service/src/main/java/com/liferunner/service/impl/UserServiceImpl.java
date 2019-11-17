package com.liferunner.service.impl;

import com.liferunner.dto.UserAddressRequestDTO;
import com.liferunner.dto.UserRequestDTO;
import com.liferunner.enums.SexEnum;
import com.liferunner.mapper.UserAddressMapper;
import com.liferunner.mapper.UsersMapper;
import com.liferunner.pojo.UserAddress;
import com.liferunner.pojo.Users;
import com.liferunner.service.IUserService;
import com.liferunner.utils.MD5GeneratorTools;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

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
    private final UserAddressMapper addressMapper;

    @Autowired
    public UserServiceImpl(UsersMapper usersMapper, Sid sid, UserAddressMapper addressMapper) {
        this.usersMapper = usersMapper;
        this.sid = sid;
        this.addressMapper = addressMapper;
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
                .sex(SexEnum.SECRET.type)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        this.usersMapper.insertSelective(user);
        log.info("======end create user : {}=======", userRequestDTO);
        return user;
    }

    @Override
    public Users userLogin(UserRequestDTO userRequestDTO) throws Exception {
        log.info("======用户登录请求：{}", userRequestDTO);
        Example example = new Example(Users.class);
        val condition = example.createCriteria();
        condition.andEqualTo("username", userRequestDTO.getUsername());
        condition.andEqualTo("password", MD5GeneratorTools.getMD5Str(userRequestDTO.getPassword()));
        // 千万记住这里传入的是Example对象值，而不是condition
        //https://blog.csdn.net/pseudonym_/article/details/100112223
        val user = this.usersMapper.selectOneByExample(example);
        log.info("======用户登录处理结果：{}", user);
        return user;
    }

    @Override
    public List<UserAddress> getAddressByUserId(String uid) {
        return this.addressMapper.select(
                new UserAddress()
                        .builder()
                        .userId(uid)
                        .build()
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addAddress(UserAddressRequestDTO addressRequestDTO) {
        // 判断是否存在默认地址
        Integer isDefault = 0;
        val userAddressList = getAddressByUserId(addressRequestDTO.getUserId());
        if (CollectionUtils.isEmpty(userAddressList)) {
            isDefault = 1;
        }
        val userAddress = new UserAddress()
                .builder()
                .id(sid.nextShort())
                .isDefault(isDefault)
                .createdTime(new Date())
                .updatedTime(new Date())
                .build();
        // 通过工具类直接copy相同属性
        BeanUtils.copyProperties(addressRequestDTO, userAddress);
        this.addressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateAddress(UserAddressRequestDTO addressRequestDTO) {
        val userAddress = new UserAddress()
                .builder()
                .updatedTime(new Date())
                .build();
        // 通过工具类直接copy相同属性
        BeanUtils.copyProperties(addressRequestDTO, userAddress);
        Example example = new Example(UserAddress.class);
        val condition = example.createCriteria();
        condition.andEqualTo("id", addressRequestDTO.getAddressId());
        this.addressMapper.updateByExampleSelective(userAddress, example);
    }
}
