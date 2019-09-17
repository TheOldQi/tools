package com.xiafei.tools.spi;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2019/4/28 20:28</P>
 * <P>UPDATE DATE: </P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class CrySpi implements ISpi{
    @Override
    public void sayHello(String name) {
        System.out.println(name + ": 55555...");
    }
}
