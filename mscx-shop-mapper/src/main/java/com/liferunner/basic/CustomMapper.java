package com.liferunner.basic;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * CustomMapper for : 自定義basic mapper
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
public interface CustomMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
