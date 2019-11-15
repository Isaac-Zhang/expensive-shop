package com.liferunner.custom;

import com.liferunner.dto.IndexProductDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * ProductCustomMapper for : 自定义商品Mapper
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/15
 */
public interface ProductCustomMapper {
    List<IndexProductDTO> getIndexProductDtoList(@Param("paramMap") Map<String,Integer> paramMap);
}
