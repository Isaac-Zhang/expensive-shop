package com.liferunner.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * ApiApplication for : api启动类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.liferunner.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.liferunner", "org.n3r.idworker"})
public class ApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApiApplication.class)
                .run(args);
    }
}
