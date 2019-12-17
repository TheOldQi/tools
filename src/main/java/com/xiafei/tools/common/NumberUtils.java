package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

/**
 * <P>Description: 数字工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/5/14</P>
 * <P>UPDATE DATE: 2018/5/14</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class NumberUtils {

    /**
     * 单位.
     */
    private static final String UNIT[] = {"万", "千", "佰", "拾", "亿", "千", "佰",
            "拾", "万", "千", "佰", "拾", "元", "角", "分"};

    /**
     * 数字.
     */
    private static final String NUM[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆",
            "柒", "捌", "玖"};
    // 最大数值，和unit[]数组对应.
    private static final double MAX_VALUE = 9999999999999.99D;

    public static String toChinese(double number) {
        if (number < 0 || number > MAX_VALUE)
            throw new IllegalArgumentException("参数非法");
        final long money1 = Math.round(number * 100); // 四舍五入到分
        if (money1 == 0)
            return "零元整";
        final String strMoney = String.valueOf(money1);

        boolean isZero = false; // 用于判断当前为是否为零
        String result = "";
        for (int numIndex = 0, unitIndex = UNIT.length - strMoney.length();
             numIndex < strMoney.length(); numIndex++, unitIndex++) {
            char num = strMoney.charAt(numIndex);
            if (num == '0') {
                isZero = true;
                if (UNIT[unitIndex] == "亿" || UNIT[unitIndex] == "万"
                        || UNIT[unitIndex] == "元") { // 如果当前位是亿、万、元，且数值为零
                    result = result + UNIT[unitIndex]; //补单位亿、万、元
                    isZero = false;
                }
            } else {
                if (isZero) {
                    result = result + "零";
                    isZero = false;
                }
                result = result + NUM[Integer.parseInt(String.valueOf(num))] + UNIT[unitIndex];
            }
        }
        //不是角分结尾就加"整"字
        if (!result.endsWith("角") && !result.endsWith("分")) {
            result = result + "整";
        }
        //例如没有这行代码，数值"400000001101.2"，输出就是"肆千亿万壹千壹佰零壹元贰角"
        result = result.replaceAll("亿万", "亿");
        return result;
    }


}
