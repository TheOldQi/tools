package com.xiafei.tools.common.red;

import java.math.BigDecimal;
import java.util.Random;

/**
 * <P>Description: 生成金额较均匀红包金额数组. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/17 10:49</P>
 * <P>UPDATE AT: 2019/1/17 10:49</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class BalanceRed extends RedAmountArrayGenerator {

    public static void main(String[] args) {
        BalanceRed b = new BalanceRed();
        for(int i = 0;i<100000;i++){

            BigDecimal[] gen = b.gen(new BigDecimal("10012.10"), new BigDecimal("10000"), new BigDecimal("0.01"), new BigDecimal("12.00"));
        }
    }

    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ONE_FEN = new BigDecimal("0.01");


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
    void normalFill(final BigDecimal[] result, final BigDecimal amount, final BigDecimal num, final BigDecimal minAmount,
                    final BigDecimal maxAmount, final BigDecimal diffDecimal) {
        final Random r = new Random();
        // 目标期望值
        final BigDecimal targetExpect = amount.divide(num, 2, BigDecimal.ROUND_DOWN);

        // 最大金额和目标期望值之间的期望值
        final BigDecimal maxSubtract = maxAmount.subtract(targetExpect);
        final double maxDiff = maxSubtract.doubleValue();
        final BigDecimal maxExpect = maxSubtract.divide(TWO, 2, BigDecimal.ROUND_DOWN).add(targetExpect);

        // 最小金额和目标期望值之间的期望值
        final BigDecimal minSubtract = targetExpect.subtract(minAmount);
        final double minDiff = minSubtract.doubleValue();
        final BigDecimal minExpect = minSubtract.divide(TWO, 2, BigDecimal.ROUND_DOWN).add(minAmount);

        final double dif = diffDecimal.doubleValue();
        int index = 0;
        int numInt = num.intValue();
        BigDecimal usedAmount = BigDecimal.ZERO;
        out:
        do {

            if (index == (numInt - 1)) {
                BigDecimal subtract = amount.subtract(usedAmount);
                if (subtract.compareTo(maxAmount) > 0) {
                    result[index] = maxAmount;
                    usedAmount = usedAmount.add(maxAmount);
                } else {
                    result[index] = subtract;
                    usedAmount = amount;
                }
                break;
            }

            final BigDecimal remain = num.subtract(new BigDecimal(index));
            // 先生成一次随机数
            BigDecimal rn = nextRn(r, dif, minAmount, maxAmount, remain, amount.subtract(usedAmount));

            result[index++] = rn;
            usedAmount = usedAmount.add(rn);

            if (index == (numInt - 1)) {
                BigDecimal subtract = amount.subtract(usedAmount);
                if (subtract.compareTo(maxAmount) > 0) {
                    result[index] = maxAmount;
                    usedAmount = usedAmount.add(maxAmount);
                } else {
                    result[index] = subtract;
                    usedAmount = amount;
                }
                break;
            }


            BigDecimal rDiff = rn.subtract(targetExpect);
            if (rDiff.compareTo(BigDecimal.ZERO) > 0) {

                int i = rDiff.divide(minSubtract, 0, BigDecimal.ROUND_UP).intValue();
                for (int j = 0; j < i; j++) {
                    if (j == (i - 1)) {
                        BigDecimal last = targetExpect.subtract(rDiff);
                        result[index++] = last;
                        usedAmount = usedAmount.add(last);

                        if (index == (numInt - 1)) {
                            BigDecimal subtract = amount.subtract(usedAmount);
                            if (subtract.compareTo(maxAmount) > 0) {
                                result[index] = maxAmount;
                                usedAmount = usedAmount.add(maxAmount);
                            } else {
                                result[index] = subtract;
                                usedAmount = amount;
                            }
                            break out;
                        }

                    } else {
                        if ((numInt - index - i + j) > 0) {
                            final BigDecimal rm = new BigDecimal(r.nextDouble() * minDiff).setScale(2, BigDecimal.ROUND_DOWN);
                            final BigDecimal first = targetExpect.subtract(rm);
                            result[index++] = first;
                            usedAmount = usedAmount.add(first);

                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }

                            BigDecimal second = targetExpect.subtract(minSubtract.subtract(rm));
                            result[index++] = second;
                            usedAmount = usedAmount.add(second);

                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }

                        } else {
                            result[index++] = targetExpect;
                            usedAmount = usedAmount.add(targetExpect);


                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }

                        }

                        rDiff = rDiff.subtract(minSubtract);
                    }
                }
            } else if (rDiff.compareTo(BigDecimal.ZERO) < 0) {
                rDiff = rDiff.multiply(new BigDecimal("-1"));
                int i = rDiff.divide(maxSubtract, 0, BigDecimal.ROUND_UP).intValue();
                for (int j = 0; j < i; j++) {
                    if (j == (i - 1)) {
                        BigDecimal last = targetExpect.add(rDiff);
                        result[index++] = last;
                        usedAmount = usedAmount.add(last);
                        if (index == (numInt - 1)) {
                            BigDecimal subtract = amount.subtract(usedAmount);
                            if (subtract.compareTo(maxAmount) > 0) {
                                result[index] = maxAmount;
                                usedAmount = usedAmount.add(maxAmount);
                            } else {
                                result[index] = subtract;
                                usedAmount = amount;
                            }
                            break out;

                        }

                    } else {
                        if ((numInt - index - i + j) > 0) {
                            final BigDecimal rm = new BigDecimal(r.nextDouble() * maxDiff).setScale(2, BigDecimal.ROUND_DOWN);

                            final BigDecimal first = targetExpect.add(rm);
                            result[index++] = first;
                            usedAmount = usedAmount.add(first);
                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }

                            BigDecimal second = targetExpect.add(maxSubtract.subtract(rm));
                            result[index++] = second;
                            usedAmount = usedAmount.add(second);
                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }


                        } else {
                            result[index++] = targetExpect;
                            usedAmount = usedAmount.add(targetExpect);

                            if (index == (numInt - 1)) {
                                BigDecimal subtract = amount.subtract(usedAmount);
                                if (subtract.compareTo(maxAmount) > 0) {
                                    result[index] = maxAmount;
                                    usedAmount = usedAmount.add(maxAmount);
                                } else {
                                    result[index] = subtract;
                                    usedAmount = amount;
                                }
                                break out;

                            }

                        }

                        rDiff = rDiff.subtract(maxSubtract);

                    }
                }
            }

        } while (index < numInt);
        BigDecimal scurfAmount = amount.subtract(usedAmount);
        if (scurfAmount.compareTo(BigDecimal.ZERO) > 0) {
            clearScurf(result, maxAmount, scurfAmount, r);

        }
    }

    private void clearScurf(BigDecimal[] result, BigDecimal maxAmount, BigDecimal scurfAmount, final Random r) {
        while (true) {
            int i = r.nextInt(result.length);
            BigDecimal space = maxAmount.subtract(result[i]);
            BigDecimal scurfSubSpace = scurfAmount.subtract(space);
            if (scurfSubSpace.compareTo(BigDecimal.ZERO) > 0) {
                result[i] = maxAmount;
                scurfAmount = scurfAmount.subtract(space);
            } else {
                result[i] = result[i].add(scurfAmount);
                break;
            }
        }
    }


    private BigDecimal nextRn(final Random r, double dif, BigDecimal minAmount, BigDecimal maxAmount,
                              final BigDecimal remainNum, final BigDecimal remainAmount) {

        // 先算出随机数最小最大值
        final BigDecimal otherNum = remainNum.subtract(BigDecimal.ONE);
        BigDecimal minOther = minAmount.multiply(otherNum);
        BigDecimal maxOther = maxAmount.multiply(otherNum);
        final BigDecimal rnMin = remainAmount.subtract(maxOther);
        final BigDecimal rnMax = remainAmount.subtract(minOther);

        BigDecimal rn = new BigDecimal(r.nextDouble() * dif).add(minAmount).setScale(2, BigDecimal.ROUND_DOWN);
        while (true) {
            if (rn.compareTo(rnMax) > 0) {
                dif -= 0.01;

            } else if (rn.compareTo(rnMin) < 0) {
                dif -= 0.01;
                minAmount = minAmount.add(ONE_FEN);
            } else {
                break;
            }
            rn = new BigDecimal(r.nextDouble() * dif).add(minAmount).setScale(2, BigDecimal.ROUND_DOWN);
        }
        return rn;
    }

}
