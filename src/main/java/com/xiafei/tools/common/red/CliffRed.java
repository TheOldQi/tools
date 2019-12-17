package com.xiafei.tools.common.red;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;

/**
 * <P>Description: 陡峭类红包生成工具，不含人工干预，若单个最小和单个最大的随机期望值 （maxAmount -  minAmount)/2 + minAmount 与金额的期望值相差较大，
 * * 会出现大部分是最大或大部分是最小金额的数据，当希望红包金额完全随机没有对概率的人为干涉时使用这个方法。. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/17 10:44</P>
 * <P>UPDATE AT: 2019/1/17 10:44</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public final class CliffRed extends RedAmountArrayGenerator {

    public static void main(String[] args) {
        CliffRed b = new CliffRed();
        for(int i = 0;i<100000;i++){

            BigDecimal[] gen = b.gen(new BigDecimal("10012.10"), new BigDecimal("10000"), new BigDecimal("0.01"), new BigDecimal("12.00"));
        }
    }

    /**
     * 非特殊数字填充.
     *
     * @param result      结果数组
     * @param amount      总金额
     * @param num         数组长度
     * @param minAmount   单个最小金额
     * @param maxAmount   单个最大金额
     * @param diffDecimal 最大最小金额之差
     */
    @Override
    protected void normalFill(final BigDecimal[] result, final BigDecimal amount, final BigDecimal num,
                              final BigDecimal minAmount, final BigDecimal maxAmount, final BigDecimal diffDecimal) {


        BigDecimal calAmount = amount;
        BigDecimal usedAmount = BigDecimal.ZERO;
        final double diff = diffDecimal.doubleValue();
        int numInt = num.intValue();
        int index = 0;
        final Random random = new Random();

        do {

            // 剩余未处理的数量
            final BigDecimal calNum = num.subtract(new BigDecimal(index));

            // 最大金额最多出现次数,由二元二次方程推导而出，设最大金额出现次数x，最小金额出现次数y，
            // 因总金额固定的情况下，出现最多最大金额的时刻也是出现最多最小金额的时刻，故有：
            // max * x  + min * y = amount
            // x + y = num
            // 得最大金额出现最大次数 x = (amount - min * num ) / max - min
            // 最小金额出现最大次数 y = (max * num - amount) / max - min
            final BigDecimal maxAmountMaxCount = calAmount.subtract(minAmount.multiply(calNum)).divide(diffDecimal, 0, BigDecimal.ROUND_DOWN);
            // 最小金额最多出现次数
            final BigDecimal minAmountMaxCount = maxAmount.multiply(calNum).subtract(calAmount).divide(diffDecimal, 0, BigDecimal.ROUND_DOWN);

            // 可以放心随机的数量
            // 取两者较小的值，那么随机数无论多么接近最大最小值都没有问题
            int safeCount = maxAmountMaxCount.compareTo(minAmountMaxCount) > 0 ? minAmountMaxCount.intValue() : maxAmountMaxCount.intValue();
//
            if (safeCount == 0) {

                if (maxAmountMaxCount.compareTo(BigDecimal.ZERO) == 0) {
                    Arrays.fill(result, index, result.length - 1, minAmount);
                    result[result.length - 1] = calAmount.subtract(minAmount.multiply(new BigDecimal(result.length - 1 - index)));
                    return;

                } else {
                    Arrays.fill(result, index, result.length - 1, maxAmount);
                    result[result.length - 1] = calAmount.subtract(maxAmount.multiply(new BigDecimal(result.length - 1 - index)));
                    return;
                }
            } else {

                for (int i = 0; i < safeCount; i++) {
                    final BigDecimal item = new BigDecimal(random.nextDouble() * diff).add(minAmount).setScale(2, BigDecimal.ROUND_DOWN);
                    result[index++] = item;
                    usedAmount = usedAmount.add(item);
                }
            }

            calAmount = amount.subtract(usedAmount);

        } while (index < numInt);

    }


}
