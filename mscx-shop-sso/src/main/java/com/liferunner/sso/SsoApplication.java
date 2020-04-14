package com.liferunner.sso;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * SsoApplication for 单点登录启动类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/4/14
 **/
@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = {
    "com.liferunner.mapper",
    "com.liferunner.custom"
})
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.liferunner", "org.n3r.idworker"})
@EnableSwagger2Doc
public class SsoApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(SsoApplication.class)
            .run(args);
    }
}
