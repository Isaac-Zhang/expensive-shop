package com.liferunner.custom;

import com.liferunner.dto.UserCommentResponseDTO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/***
 * 自定义评价mapper
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/11/26
 */
public interface CommentCustomMapper {

    int saveUserComments(@Param("paramMap") Map<String, Object> paramMap);

    List<UserCommentResponseDTO> getUserCommentList(@Param("paramMap") Map<String, Object> paramMap);
}
