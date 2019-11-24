package com.liferunner.custom;

import com.liferunner.dto.UserOrderResponseDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * OrderCustomMapper for : 自定义订单Mapper
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/24
 */
public interface OrderCustomMapper {

    List<UserOrderResponseDTO> getUserOrderList(@Param("paramMap") Map<String, Object> paramMap);
}
