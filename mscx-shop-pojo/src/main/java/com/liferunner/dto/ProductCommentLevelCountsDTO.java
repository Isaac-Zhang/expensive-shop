package com.liferunner.dto;

import lombok.*;

/**
 * ProductCommentLevelCountsDTO for : 商品评价等级统计DTO
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductCommentLevelCountsDTO {

    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;
}
