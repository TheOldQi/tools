package com.xiafei.tools.common.red;

import com.xiafei.tools.common.ArrayUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * <P>Description: 红包金额数组生成核心逻辑. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/17 10:35</P>
 * <P>UPDATE AT: 2019/1/17 10:35</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
abstract class RedAmountArrayGenerator {


    /**
     * 生成随机金额数组（线程安全）.
     *
     * @param amount    总金额
     * @param num       数组长度
     * @param minAmount 单个最小金额
     * @param maxAmount 单个最大金额
     * @return 随机金额数组
     */
    public BigDecimal[] gen(final BigDecimal amount, final BigDecimal num, final BigDecimal minAmount,
                            final BigDecimal maxAmount) {
        log.info("生成随机金额数组,amount={},num={},minAmount={},maxAmount={}", amount, num, minAmount, maxAmount);
        checkRandomAmountParams(amount, num, minAmount, maxAmount);

        long start = System.currentTimeMillis();

        final BigDecimal[] result = new BigDecimal[num.intValue()];
        fill(result, amount, num, minAmount, maxAmount);

        ArrayUtils.shuffle(result);
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal b : result) {
            total = total.add(b);
        }
        if (total.compareTo(amount) != 0) {
            log.error("生成数组总金额与入参总金额有出入，算法有bug，实际金额={}，要求金额={}", total, amount);
            throw new RuntimeException("红包计算错误");
        }
        log.info("生成随机金额数组完成，耗时={}ms", System.currentTimeMillis() - start);
        return result;
    }

    /**
     * 随机金额生成算法入参校验.
     *
     * @param amount    总金额
     * @param num       数组长度
     * @param minAmount 单个最小金额
     * @param maxAmount 单个最大金额
     */
    private void checkRandomAmountParams(BigDecimal amount, BigDecimal num, BigDecimal minAmount, BigDecimal maxAmount) {
        if (amount.scale() != 2 || minAmount.scale() != 2 || maxAmount.scale() != 2) {
            throw new IllegalArgumentException("金额必须小数点后两位");
        }

        if (num.scale() != 0 || num.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("个数必须是正整数");
        }


        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("总金额必须大于0");
        }
        if (minAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单个最小金额必须大于0");
        }
        if (maxAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("单个最大金额必须大于0");
        }

        if (minAmount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException("单个最小金额必须不大于单笔最大金额");
        }

        if (minAmount.multiply(num).compareTo(amount) > 0) {
            throw new IllegalArgumentException("个数 * 单笔最小 不能大于总金额");
        }

        if (maxAmount.multiply(num).compareTo(amount) < 0) {
            throw new IllegalArgumentException("个数 * 单笔最大 不能小于总金额");
        }

        if (maxAmount.compareTo(amount) > 0) {
            throw new IllegalArgumentException("单笔最大金额 不能大于总金额");
        }

    }

    /**
     * 填充数字进数组.
     *
     * @param result    结果数组
     * @param amount    总金额
     * @param num       数组长度
     * @param minAmount 单个最小金额
     * @param maxAmount 单个最大金额
     */
    private void fill(final BigDecimal[] result, final BigDecimal amount, final BigDecimal num,
                      final BigDecimal minAmount, final BigDecimal maxAmount) {
        // 计算总数量的平均数
        final BigDecimal diffDecimal = maxAmount.subtract(minAmount);
        // 如果数值比较特殊，采用快速退出方法
        if (!quickOut(result, amount, num, minAmount, maxAmount, diffDecimal)) {
            // 普通的填充
            normalFill(result, amount, num, minAmount, maxAmount, diffDecimal);
        }
    }

    abstract void normalFill(final BigDecimal[] result, final BigDecimal amount, final BigDecimal num,
                             final BigDecimal minAmount, final BigDecimal maxAmount, final BigDecimal diffDecimal);

    /**
     * 如果入参是一些特殊值，使用这个方法快速出结果.
     *
     * @param result      结果数组
     * @param amount      总金额
     * @param num         数组长度
     * @param minAmount   单个最小金额
     * @param maxAmount   单个最大金额
     * @param diffDecimal 最大最小金额之差
     * @return true - 快速退出，false -  不是特殊参数，需要进行普通填充
     */
    private boolean quickOut(BigDecimal[] result, BigDecimal amount, BigDecimal num, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal diffDecimal) {
        // 计算平均值，可能有精度损失，可能会比最精确的平均值小
        final BigDecimal average = amount.divide(num, 2, BigDecimal.ROUND_DOWN);

        if (average.compareTo(maxAmount) == 0) {
            // 如果平均数正好等于最大金额，而总金额不可能大于最大金额 * 数量 ，所以没有精度损失，直接填充
            Arrays.fill(result, maxAmount);
            return true;
        }

        if (average.compareTo(minAmount) < 0) {
            // 入参已经校验过最小金额*数量一定小于等于最大金额，现在最小金额大于平均金额了，一定是精度损失了，
            // 也就是说实际上总金额一定就等于最小金额 * 数量
            Arrays.fill(result, minAmount);
            return true;
        }

        if (average.compareTo(minAmount) == 0) {
            // 因为在计算平均数时精度有损失，所以这里用平均数乘以个数反算，再次校验
            final BigDecimal reverseTotal = average.multiply(num);
            if (reverseTotal.compareTo(amount) == 0) {
                // 如果反算总金额等于实际总金额，直接填充
                Arrays.fill(result, minAmount);
                return true;
            } else {
                // 说明精度有损失，先填充所有
                Arrays.fill(result, 0, result.length, average);
                // 计算实际总金额和反算总金额的差额，分配到各个数字上
                final BigDecimal subtract = amount.subtract(reverseTotal);
                if (subtract.compareTo(BigDecimal.ZERO) > 0) {
                    // 用round_up确保计算的数字正确
                    final int distNum = subtract.divide(diffDecimal, 0, BigDecimal.ROUND_UP).intValue();
                    for (int i = 0; i < distNum; i++) {
                        final BigDecimal add;

                        if (i == (distNum - 1)) {
                            // 最后一个填充需要反向计算确保总金额一致
                            add = subtract.subtract(diffDecimal.multiply(new BigDecimal(distNum - 1)));

                        } else {
                            //其他只需要填充diffDecimal进去即可
                            add = diffDecimal;
                        }
                        result[i] = result[i].add(add);
                    }

                }
                return true;

            }
        }


        return false;
    }


    protected RedAmountArrayGenerator() {
    }

}
