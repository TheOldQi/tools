package com.xiafei.tools.test;

import com.xiafei.tools.nosql.redis.JedisApplication;
import com.xiafei.tools.nosql.redis.JedisSerialNo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/9 17:58</P>
 * <P>UPDATE AT: 2019/1/9 17:58</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = JedisApplication.class)
@ActiveProfiles("dev")
public class JedisSerialNoTest {

    @Resource
    private JedisSerialNo jedisSerialNo;


    @Test
    public void test() {
        Set<String> set = new HashSet<>();
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        long start = System.currentTimeMillis();
        final List<Thread> threadList = new ArrayList<>();
        for (int t = 0; t < 100; t++) {
            threadList.add(new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 10000; i++) {
                        String test = jedisSerialNo.nexValPreToDay("test",20);
                        queue.add(test);
//            System.out.print("\t" + test);
//            if (i % 5 == 0) {
//                System.out.println();
//            }
                    }
                }
            });
        }

        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int index = 1;
        for (String s : queue) {
            boolean add = set.add(s);
            if (!add) {
                System.out.println("找到重复序列号" + s);
            }

        }

        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void testSync() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            String test = jedisSerialNo.nexValPreToDay("test",20);
//            System.out.print("\t" + test);
//            if (i % 5 == 0) {
//                System.out.println();
//            }
        }

        System.out.println(System.currentTimeMillis() - start);
    }

}
