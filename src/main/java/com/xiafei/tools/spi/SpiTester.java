package com.xiafei.tools.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2019/4/28 20:32</P>
 * <P>UPDATE DATE: </P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class SpiTester {

    public static void main(String[] args) {

        while(true){
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("加载实现类");
            ServiceLoader<ISpi> spi = ServiceLoader.load(ISpi.class);
            System.out.println("实现类加载完成");
            Iterator<ISpi> iterator = spi.iterator();
            while(iterator.hasNext()){

                iterator.next().sayHello("齐霞飞");
            }

            System.out.println("睡5秒");
        }


    }
}
