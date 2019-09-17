package com.xiafei.tools.nosql.redis;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * <P>Description: 用于测试的启动类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/9 18:17</P>
 * <P>UPDATE AT: 2019/1/9 18:17</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class JedisApplication {
}
