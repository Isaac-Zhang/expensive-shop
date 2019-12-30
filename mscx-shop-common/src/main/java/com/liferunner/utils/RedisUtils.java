package com.liferunner.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/***
 * redis 操作工具类
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/12/30
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisUtils {

    private final StringRedisTemplate redisTemplate;

    /**
     * 新增string,key
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param key 主键
     * @param value 内容
     * @throws
     */
    public void set(String key,String value){
        this.redisTemplate.opsForValue().set(key,value);
    }

    /***
     * 根据key获取value
     *
     * @author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
     * @param key 主键
     * @return java.lang.String 结果
     * @throws
     */
    public String get(String key){
        return this.redisTemplate.opsForValue().get(key);
    }

}
