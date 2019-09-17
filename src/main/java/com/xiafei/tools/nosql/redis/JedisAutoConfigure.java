package com.xiafei.tools.nosql.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * <P>Description: 自动配置jedis. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/10/16</P>
 * <P>UPDATE DATE: 2018/10/16</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Configuration
@ConditionalOnClass(JedisClient.class)
@EnableConfigurationProperties(JedisClientProperties.class)
public class JedisAutoConfigure {

    @Resource
    private JedisClientProperties properties;

    /**
     * 初始化JedisClient对象.
     *
     * @return 返回什么并不重要.
     * @throws Exception 连接redis失败什么的
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jedis.client", name = "enable", havingValue = "true")
    public JedisClient jedisClient() throws Exception {
        return new JedisClient(properties.getAddress(), properties.getPassword(), properties.getMaxTotal(),
                properties.getMaxIdle(), properties.getMaxWaitTimeMs(), properties.getPrefix());
    }



}
