package com.liferunner.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 用户评价返回展示DTO
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
public class UserCommentResponseDTO {

    private String commentId;
    private String content;
    private Date createdTime;
    private String productId;
    private String productName;
    private String productSpecName;
    private String productImg;
}
