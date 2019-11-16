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
    private Integer commentLevel;
    private String specName;
    private String content;
    private Date createdTime;
    private String userFace;
    private String nickname;
}
