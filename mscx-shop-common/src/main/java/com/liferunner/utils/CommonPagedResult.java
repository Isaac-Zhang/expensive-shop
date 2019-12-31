package com.liferunner.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CommonPagedResult for : 通用分页处理组件
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonPagedResult {

    /**
     * 当前页数
     */
    private int pageNumber;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 数据总条数
     */
    private long records;
    /**
     * 每行显示的具体数据对象
     */
    private List<?> rows;
}
