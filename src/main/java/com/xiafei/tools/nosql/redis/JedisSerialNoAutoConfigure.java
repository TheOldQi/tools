package com.xiafei.tools.nosql.redis;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/9 15:48</P>
 * <P>UPDATE AT: 2019/1/9 15:48</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@AutoConfigureAfter(value = JedisAutoConfigure.class)
@Configuration
public class JedisSerialNoAutoConfigure {

    @Bean
    public JedisSerialNo jedisSerialNo() {
        return new JedisSerialNo();
    }
}
