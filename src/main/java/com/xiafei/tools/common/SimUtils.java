package com.xiafei.tools.common;

import java.math.BigDecimal;

/**
 * <P>Description: 相似度工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2019/5/29 14:23</P>
 * <P>UPDATE DATE: </P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class SimUtils {


    /**
     * levensthtein大神发明的字符串编辑距离相似度算法.
     *
     * @param str1 参与比较字符串1
     * @param str2 参与比较字符串2
     * @return 总分100，最低0分
     */
    public static int levenshtein(final String str1, final String str2) {
        if (isBlank(str1) || isBlank(str2)) return 0;

        int x = str1.length();
        int y = str2.length();
        int max = Math.max(x, y);
        int[][] ds = new int[x + 1][y + 1];
        initDs(ds);
        for (int i = 1; i <= x; i++) {
            final char c1 = str1.charAt(i - 1);
            for (int j = 1; j <= y; j++) {
                final char c2 = str2.charAt(j - 1);
                if (c1 != c2) ds[i][j] = min(ds[i - 1][j - 1] + 1, ds[i][j - 1] + 1, ds[i - 1][j] + 1);
                else ds[i][j] = min(ds[i - 1][j - 1], ds[i][j - 1] + 1, ds[i - 1][j] + 1);
            }
        }

        return BigDecimal.ONE.subtract(new BigDecimal(ds[x][y]).
                divide(new BigDecimal(max), 2, BigDecimal.ROUND_DOWN)).multiply(new BigDecimal(100)).intValue();
    }

    private static int min(int... ints) {
        int temp = ints[0];
        for (int i = 1, len = ints.length; i < len; i++) {
            if (temp > ints[i]) {
                temp = ints[i];
            }
        }
        return temp;
    }

    /**
     * 将distance二维数组矩阵的第一排，第一列初始化
     *
     * @param ds distance数组
     */
    private static void initDs(final int[][] ds) {
        int x = ds.length;
        int y = ds[0].length;
        /**
         * 初始化第一行.
         */
        for (int i = 0; i < x; i++) {
            ds[i][0] = i;
        }
        /**
         * 初始化第一列.
         */
        for (int i = 0; i < y; i++) {
            ds[0][i] = i;
        }

    }


    private static boolean isBlank(final String s) {
        return s == null || s.trim().equals("");
    }

}
