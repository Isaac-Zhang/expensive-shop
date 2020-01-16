package com.liferunner.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserRequestDTO for : 用户DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "创建用户DTO", description = "用户注册需要的参数对象")
public class UserRequestDTO {

    @ApiModelProperty(value = "用户名", notes = "username", example = "isaaczhang", required = true)
    private String username;
    @ApiModelProperty(value = "注册密码", notes = "password", example = "12345678", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", notes = "confimPassword", example = "12345678", required = false)
    private String confirmPassword;
}
