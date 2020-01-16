package com.liferunner.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserResponseDTO for : 返回的用户信息
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "用户信息返回DTO", description = "用户登录成功后需要的返回对象")
public class UserResponseDTO {

    /**
     * 主键id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String face;

    /**
     * 性别  1:男  0:女  2:保密
     */
    private Integer sex;
}