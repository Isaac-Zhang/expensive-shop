package com.liferunner.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * ApiApplication for : api启动类
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/3
 */
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApiApplication.class)
                .run(args);
    }
}
