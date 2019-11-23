package com.liferunner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * ProductCommentDTO for : 商品评价DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCommentDTO {
    //评价等级
    private Integer commentLevel;
    //规格名称
    private String specName;
    //评价内容
    private String content;
    //评价时间
    private Date createdTime;
    //用户头像
    private String userFace;
    //用户昵称
    private String nickname;
}
