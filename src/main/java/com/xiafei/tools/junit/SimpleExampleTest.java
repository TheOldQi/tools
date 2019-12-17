package com.xiafei.tools.junit;

import org.springframework.test.context.ContextConfiguration;

/**
 * <P>Description: 普通单元测试，不需要mock的，不要指定initializers，则spring-test的TestContext会重用相同locations配置的spring上下文，提升测试运行速度. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   </P>
 * <P>CREATE DATE: 2019/6/27 10:51</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@ContextConfiguration(locations = "classpath:spring/spring-main.xml")
public class SimpleExampleTest extends JunitTestBase {
}
