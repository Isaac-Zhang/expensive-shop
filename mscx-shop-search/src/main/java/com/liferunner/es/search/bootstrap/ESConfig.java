package com.liferunner.es.search.bootstrap;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * ESConfig
 *
 * @author <a href="mailto:magicianisaac@gmail.com">Isaac.Zhang | 若初</a>
 * @since 2020/5/13
 **/
@Configuration
public class ESConfig {

    /**
     * 解决netty引起的issue
     */
    @PostConstruct
    void init(){
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }
}
