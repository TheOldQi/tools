package com.xiafei.tools.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <P>Description:生成序列号服务.
 * 序列号组成：19位年月日时分秒毫秒 yyyyMMddHHmmssSSSSS + 8位16进制本机ip + 5位流水号，共32位
 * 每个系统可以支持每毫秒9万9千9百9十9次请求
 * 注：支持分布式系统</P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/3/6</P>
 * <P>UPDATE DATE: 2018/3/6</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class SerialNo {
    // 当前流水号.
    private static AtomicInteger currentNo = new AtomicInteger(0);
    // 最大流水号.
    private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyyMMddHHmmssSSSSS");
    private static final int MAX = 99999;
    private static String IP_HEX = "";

    static {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("本机ip加载失败!使用127.0.0.1");
        }
        final String[] ipFra = ip.split("\\.");
        for (String frag : ipFra) {
            String hexString = Integer.toHexString(Integer.parseInt(frag)).toUpperCase();
            hexString = hexString.length() == 1 ? 0 + hexString : hexString;
            IP_HEX += hexString;
        }
    }

    /**
     * 获取序列号.
     *
     * @return 序列号
     */
    public static String nextVal() {
        return SDF.format(System.currentTimeMillis()).concat(IP_HEX).concat(getLastFive());
    }

    private static String getLastFive() {
        int no = currentNo.incrementAndGet();
        if (no > MAX) {
            if (currentNo.compareAndSet(no, 1)) {
                no = 1;
            } else {
                no = currentNo.incrementAndGet();
            }
        }
        return String.format("%05d", no);
    }

    public static void main(String[] args) {
        System.out.println(nextVal());
    }
}
