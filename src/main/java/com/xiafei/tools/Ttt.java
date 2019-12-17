package com.xiafei.tools;

import java.util.function.Supplier;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   </P>
 * <P>CREATE DATE: 2019/6/28 11:06</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class Ttt {
    public static void main(String[] args) {
        System.out.println(fibonaqie(16));
        System.out.println(fibonaqieQuick(16));
        System.out.println(fibonaqieQuick(64));

    }

    public static int fibonaqie(int n) {
        if (n == 1 || n == 2) {
            return 1;
        }

        return fibonaqie(n - 1) + fibonaqie(n - 2);
    }

    public static int fibonaqieQuick(int n) {
        int i1 = 1;
        int i2 = 1;
        int temp = 0;

        for (int i = 2; i < n; i++) {
            temp = i2;
            i2 = i1 + i2;
            i1 = temp;
            System.out.print(i1 + ",");
            System.out.println(i2);
        }
        return i2;
    }


}
