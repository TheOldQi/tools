package com.xiafei.tools.algorithm;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <P>Description: 查找连续的. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2020/1/6 下午8:10</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public final class FindConsecutive {


    public static Result findConsecutiveOrderNo(String text, int stopChars, TargetEnum targetEnum) {
        if (text == null || "".equals(text)) return Result.empty();
        if (stopChars < 0) {
            stopChars = 0;
        }

        final char[] chars = text.toCharArray();
        boolean cycleFlag = false;
        for (int i = stopChars, len = chars.length; i < len; i++) {
//            if(chars)
        }

        return null;

    }


    @Data
    public static class Result implements Serializable {
        private static Result EMPTY = new Result();

        public static Result empty() {
            return EMPTY;
        }

        private List<Position> pos;

    }

    @Data
    public static class Position implements Serializable {
        /**
         * 从第几个字符开始（包含），从0开始计算.
         */
        private int from;
        /**
         * 到第几个字符结束（不包含），从0开始计算.
         */
        private int to;
    }

    /**
     * <P>Description: 目标枚举. </P>
     * <P>CALLED BY:   齐霞飞 </P>
     * <P>UPDATE BY:    </P>
     * <P>CREATE DATE: 2020/1/6 下午8:14</P>
     * <P>UPDATE DATE: </P>
     *
     * @author 齐霞飞
     * @version 1.0
     * @since java 1.8.0
     */
    public enum TargetEnum {

        NUMBER_ONLY, NUMBER_AND_CHAR

    }
}
