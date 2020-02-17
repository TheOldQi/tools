package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <P>Description: 计算qps. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2020/1/2 下午2:11</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class QpsCounter {

    private AtomicInteger count = new AtomicInteger(0);
    private long elapseMillis;
    private volatile long startMillis;


    private QpsCounter() {
        init();
    }

    private void init() {
        count = new AtomicInteger(0);
        elapseMillis = 0L;
        startMillis = 0L;
    }

    public static QpsCounter getInstance() {

        return new QpsCounter();
    }

    /**
     * 计数.
     */
    public void count() throws InterruptedException {

        final int i = count.getAndIncrement();
        if (i == 0) {
            startMillis = System.currentTimeMillis();
        } else {
            if (startMillis == 0) {
                while (true) {
                    if (startMillis > 0) {
                        break;
                    }

                    TimeUnit.MILLISECONDS.sleep(1);
                }
            }

            elapseMillis = System.currentTimeMillis() - startMillis;
        }
    }

    /**
     * 拿到qps.
     *
     * @return
     */
    public double getQps() {
        return new BigDecimal(count.get()).multiply(new BigDecimal(1000)).
                divide(new BigDecimal(elapseMillis), 4, RoundingMode.DOWN).doubleValue();
    }


    public void reset() {
        init();

    }

    public static void main(String[] args) throws InterruptedException {
        QpsCounter qpsCounter = getInstance();

        for (int i = 0; i < 1000; i++) {
            qpsCounter.count();
        }

        System.out.println(qpsCounter.getQps());
    }
}
