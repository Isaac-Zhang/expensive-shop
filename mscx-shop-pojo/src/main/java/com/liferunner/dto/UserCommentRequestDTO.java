package com.liferunner.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户评价对象DTO
 *
 * @Company GeekPlus
 * @Project expensive-shop
 * @Author <a href="mailto:zhangpan@geekplus.com.cn">Isaac.Zhang | 若初</a>
 * @Date 2019/11/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@ApiModel(value = "用户评价需要传递的对象")
public class UserCommentRequestDTO {

    @ApiModelProperty(notes = "评价id", value = "评价id")
    private String commentId;
    private String productId;
    private String productName;
    private String productSpecId;
    private String productSpecName;
    private Integer commentLevel;
    private String content;

}
