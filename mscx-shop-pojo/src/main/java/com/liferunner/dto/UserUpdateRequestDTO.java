package com.liferunner.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value="用户修改DTO", description="用户修改需要的参数对象")
public class UserUpdateRequestDTO {

    @ApiModelProperty(value="用户名", name="username", example="isaac", required = false)
    private String username;
    @ApiModelProperty(value="密码", name="password", example="12345678", required = false)
    private String password;
    @ApiModelProperty(value="确认密码", name="confirmPassword", example="12345678", required = false)
    private String confirmPassword;

    @NotBlank(message = "用户昵称不能为空")
    @Length(max = 12, message = "用户昵称不能超过12位")
    @ApiModelProperty(value="用户昵称", name="nickname", example="IsaacZhang", required = false)
    private String nickname;

    @Length(max = 12, message = "用户真实姓名不能超过12位")
    @ApiModelProperty(value="真实姓名", name="realname", example="IsaacZhang", required = false)
    private String realname;

    @Pattern(regexp = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$", message = "手机号格式不正确")
    @ApiModelProperty(value="手机号", name="mobile", example="13999999999", required = false)
    private String mobile;

    @Email
    @ApiModelProperty(value="邮箱地址", name="email", example="isaac@sxzhongf.com", required = false)
    private String email;

    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 2, message = "性别选择不正确")
    @ApiModelProperty(value="性别", name="sex", example="0:女 1:男 2:保密", required = false)
    private Integer sex;
    @ApiModelProperty(value="生日", name="birthday", example="1900-01-01", required = false)
    private Date birthday;
}