package com.xiafei.tools.junit;

import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * <P>Description: 单元测试基类，一个项目一个就可以，实现ApplicationContextInitializer不为别的，就为应用上下文刷新. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   </P>
 * <P>CREATE DATE: 2019/6/26 21:09</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = "test")
@TransactionConfiguration(transactionManager = "transactionManager")
public class JunitTestBase implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    /**
     * 在容器刷新前执行.
     *
     * @param configurableApplicationContext
     */
    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {

    }
}
