package com.liferunner.api.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

/**
 * CORSConfig for : 实现跨域请求配置
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2019/11/6
 */
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "shop.cors")
@Data
@AllArgsConstructor
public class CORSConfig {

    /*
     * 请求来源
     */
    private String allowOrigin;

    /**
     * 认证信息
     */
    private Boolean allowCredentials;

    /**
     * 请求方式（GET/POST等等，*表示全部）
     */
    private String allowedMethod;

    /**
     * 请求头设置，*表示全部
     */
    private String allowedHeader;
}
