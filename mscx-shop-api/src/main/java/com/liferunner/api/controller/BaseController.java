package com.liferunner.api.controller;

import org.springframework.stereotype.Controller;

/**
 * BaseController for : controller 基类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/16
 */
@Controller
public class BaseController {

    /**
     * 默认展示第1页
     */
    public final Integer DEFAULT_PAGE_NUMBER = 1;
    /**
     * 默认每页展示10条数据
     */
    public final Integer DEFAULT_PAGE_SIZE = 10;
}
