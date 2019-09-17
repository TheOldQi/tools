package com.xiafei.tools.common;

import java.util.Random;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/18 14:42</P>
 * <P>UPDATE AT: 2019/1/18 14:42</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class ArrayUtils {

    /**
     * 将数组随机打乱.
     *
     * @param target 要打乱的数组
     */
    public static void shuffle(Object[] target) {
        final Random random = new Random();
        for (int i = 0, len = target.length; i < len; i++) {
            int j;
            do {
                j = (int) (random.nextDouble() * len);
            } while (i == j);

            // 交换两个位置.
            Object io = target[i];
            target[i] = target[j];
            target[j] = io;

        }
    }
}
