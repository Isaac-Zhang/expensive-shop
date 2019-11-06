package com.liferunner.api;

import com.liferunner.api.config.CORSConfig;
import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
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
@EnableSwagger2Doc
public class ApiApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApiApplication.class)
                .run(args);
    }

    @Autowired
    private CORSConfig corsConfig;

    /**
     * 注册跨域配置信息
     *
     * @return {@link CorsFilter}
     */
    @Bean
    public CorsFilter corsFilter() {
        val corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(this.corsConfig.getAllowOrigin());
        corsConfiguration.addAllowedMethod(this.corsConfig.getAllowedMethod());
        corsConfiguration.addAllowedHeader(this.corsConfig.getAllowedHeader());
        corsConfiguration.setAllowCredentials(this.corsConfig.getAllowCredentials());

        val urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
