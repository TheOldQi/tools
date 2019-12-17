package com.xiafei.tools.common;

import java.math.BigDecimal;

/**
 * <P>Description: 金额工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/11/12</P>
 * <P>UPDATE DATE: 2018/11/12</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class AmountUtils {

    private static final BigDecimal BIG_100 = new BigDecimal(100);

    /**
     * 元为单位的金额转成Long型的分.
     *
     * @param yuan 元为单位的金额
     * @return 分为单位的金额
     */
    public static Long yuan2Fen(final BigDecimal yuan) {
        if (yuan == null) {
            return null;
        }
        return yuan.multiply(BIG_100).setScale(0, BigDecimal.ROUND_DOWN).longValue();
    }

    /**
     * 元为单位的金额转成String型的分.
     *
     * @param yuan 元为单位的金额
     * @return 分为单位的金额
     */
    public static String yuan2FenStr(final BigDecimal yuan) {
        if (yuan == null) {
            return null;
        }
        return yuan.multiply(BIG_100).setScale(0, BigDecimal.ROUND_DOWN).toString();
    }

    /**
     * 元为单位的金额转成double型的分（可以包含小数点）.
     *
     * @param yuan  元为单位的金额
     * @param scale 转成分后小数点后保留几位
     * @return 分为单位的金额
     */
    public static Double yuan2Fen(final BigDecimal yuan, final int scale) {
        if (yuan == null) {
            return null;
        }
        return yuan.multiply(BIG_100).setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * long型的分转换成元.
     *
     * @param fen 分为单位的金额
     * @return 元为单位的金额
     */
    public static BigDecimal fen2Yuan(final Long fen) {
        if (fen == null) {
            return null;
        }
        return new BigDecimal(fen).divide(BIG_100, 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * string型的分转换成元.
     *
     * @param fen 分为单位的金额
     * @return 元为单位的金额
     */
    public static BigDecimal fen2Yuan(final String fen) {
        if (fen == null) {
            return null;
        }
        return new BigDecimal(fen).divide(BIG_100, 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * double型的分转换成元.
     *
     * @param fen   分为单位的金额
     * @param scale 结果保留的小数点位数
     * @return 元为单位的金额
     */
    public static BigDecimal fen2Yuan(final Double fen, final int scale) {
        if (fen == null) {
            return null;
        }
        return new BigDecimal(fen).divide(BIG_100, scale, BigDecimal.ROUND_DOWN);
    }

    /**
     * string型的分转换成元.
     *
     * @param fen   分为单位的金额
     * @param scale 结果保留的小数点位数
     * @return 元为单位的金额
     */
    public static BigDecimal fen2Yuan(final String fen, final int scale) {
        if (fen == null) {
            return null;
        }
        return new BigDecimal(fen).divide(BIG_100, scale, BigDecimal.ROUND_DOWN);
    }

    private AmountUtils() {
    }
}
