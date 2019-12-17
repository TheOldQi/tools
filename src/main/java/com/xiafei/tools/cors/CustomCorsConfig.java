package com.xiafei.tools.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <P>Description: 让api支持跨域请求的Cors配置. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/1</P>
 * <P>UPDATE DATE: 2017/12/1</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.7.0
 */
@Configuration
public class CustomCorsConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许的映射地址
                .allowedOrigins("*") // 允许的请求来源，如果这里设置成*，默认会将origin设置成每次请求的来源域名
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS") // 允许的提交方法
                .allowCredentials(true).// 允许Cookie跨域，在做登录校验的时候有用
                maxAge(3600); // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
    }
}
