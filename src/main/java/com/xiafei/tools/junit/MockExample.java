package com.xiafei.tools.junit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * <P>Description: 单元测试例子，注意initializers后面配上当前类可以使容器刷新，不配置则不重新加载容器. </P>
 * <P>对于spring-test 4.2以上不需要配置initializers，直接定义DirtiesContext,classMode为before_class即可</P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   </P>
 * <P>CREATE DATE: 2019/6/26 22:22</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */

@ContextConfiguration(locations = "classpath:spring/spring-main.xml", initializers = MockExample.class)
public class MockExample extends JunitTestBase {

    /**
     * 如果需要刷新容器修改mock类，添加该方法.
     */
    @BeforeClass
    public static void beforeAll() {

        MockPlugin.refreshMock(".test");
    }

    /**
     * 如果有beforeClass方法，不要忘记加上该方法，使容器恢复没有mock的状态.
     */
    @DirtiesContext
    @AfterClass
    public static void afterAll() {
        MockPlugin.refreshMock(null);

    }

}
