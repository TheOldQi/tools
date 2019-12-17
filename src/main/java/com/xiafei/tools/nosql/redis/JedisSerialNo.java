package com.xiafei.tools.nosql.redis;

import com.xiafei.tools.enums.JedisEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <P>Description: 利用jedis实现的序列号工具，可以支持更短的分布式序列号生成场景，性能不如SerialNo. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/9 15:46</P>
 * <P>UPDATE AT: 2019/1/9 15:46</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class JedisSerialNo {

    @Resource
    private JedisClient jedisClient;

    private static final String REDIS_KEY_PREFIX = "JEDIS:SERIALNO:";
    private static final String LOCK_KEY_PREFIX = "JEDIS:LOCK:SERIALNO:";
    // 序列号前14位用年月日时分秒，后面根据所需序列号位数拼接流水号
    private static final FastDateFormat YYYY_M_MDD_H_HMMSS = FastDateFormat.getInstance("yyyyMMddHHmmss");
    private static final FastDateFormat YYYY_M_MDD = FastDateFormat.getInstance("yyyyMMdd");
    private static final ConcurrentHashMap<String, NumHolder> NUM_POOL = new ConcurrentHashMap<>();

    /**
     * 根据最后流水号位数选择上限制.
     */
    private static final int[] LIMIT_ARRAY = new int[]{10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 2147483647};

    /**
     * 机器数量，0当作1，为了兼顾服务器重启情况，最好设置成机器数x2.
     */
    @Value("${jedis.serialNo.machineNum:10}")
    private int machineNum;

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * 获取下一个序列号，前序由14位年月日时分秒组成.
     *
     * @param bizKey      序列号队列的key.
     * @param totalLength 序列号总长度，必须大于14
     * @return totalLength位序列号，格式为14位yyyyMMddHHmmss + 最大10位的流水号 [+padding]
     */
    public String nexValPreToSecond(final String bizKey, int totalLength) {
        if (totalLength < 15) throw new IllegalArgumentException("年月日时分秒序列号最小15位");
        final String timeStr = YYYY_M_MDD_H_HMMSS.format(System.currentTimeMillis());
        // redisKey 由 前缀 +
        final String redisKey = REDIS_KEY_PREFIX.concat("yyyyMMddHHmmss:").concat(bizKey).concat(timeStr.substring(0, 8));
        int lastLength = totalLength - 14;
        return getResult(bizKey, timeStr, redisKey, lastLength);
    }

    /**
     * 获取下一个序列号，前序由8位年月日组成.
     *
     * @param bizKey      序列号队列的key.
     * @param totalLength 序列号总长度，必须大于8
     * @return totalLength位序列号，格式为8位yyyyMMdd + 最大10位的流水号 [+padding]
     */
    public String nexValPreToDay(final String bizKey, int totalLength) {
        if (totalLength < 9) throw new IllegalArgumentException("年月日序列号最小9位");
        final String timeStr = YYYY_M_MDD.format(System.currentTimeMillis());
        // redisKey 由 前缀 +
        final String redisKey = REDIS_KEY_PREFIX.concat("yyyyMMdd:").concat(bizKey).concat(timeStr);
        int lastLength = totalLength - 8;
        return getResult(bizKey, timeStr, redisKey, lastLength);
    }

    private String getResult(final String bizKey, final String timeStr, final String redisKey, final int lastLength) {
        if (lastLength <= 10) {
            return timeStr.concat(getLastX(redisKey, bizKey, timeStr, lastLength));
        } else {
            return timeStr.concat(getLastX(redisKey, bizKey, timeStr, 10)).concat(String.format("%0" + (lastLength - 10) + "d", random.nextInt(10)));
        }
    }

    private String getLastX(String redisKey, String bizKey, String timeStr, int lastLength) {

        NumHolder numHolder = NUM_POOL.get(redisKey);
        if (numHolder == null) {
            synchronized (this) {
                if ((numHolder = NUM_POOL.get(redisKey)) == null) {
                    numHolder = new NumHolder();
                    numHolder.setCurr(new AtomicInteger(0));
                    nextStep(numHolder, redisKey, bizKey, timeStr, lastLength);
                    NUM_POOL.put(redisKey, numHolder);
                }
            }
        }

        int curr = numHolder.getCurr().getAndIncrement();
        if (curr >= numHolder.getLimit()) {
            synchronized (this) {
                curr = numHolder.getCurr().getAndIncrement();
                if (curr >= numHolder.getLimit()) {
                    nextStep(numHolder, redisKey, bizKey, timeStr, lastLength);
                    curr = numHolder.getCurr().getAndIncrement();
                }
            }
        }

        return String.format("%0" + lastLength + "d", curr);
    }


    /**
     * 更新持有数字的当前值和限制值到下一个步长.
     *
     * @param holder
     * @param redisKey
     * @param bizKey
     * @param timeStr
     * @param lastLength
     */
    private void nextStep(final NumHolder holder, final String redisKey, final String bizKey, String timeStr, final int lastLength) {
        // redisKey自增步长后拿到上限
        // 因为前缀限制到秒了，所以最后6个不需要严格保证数据一致性，共有999999个号可用，redis平均调用响应时间2ms，
        // 一秒可以调用500次，则想要让单机跑满步长应该2000，但在分布式场景下，比如同一个bizKey有100台机器使用，最极端的情况就是99台全都获取
        // 2000步长然后再没有请求，所有请求都集中在一台机器，那么最后6位能支撑的一秒不重复的上限变成9999999 - 198000 = 801999 也属足够
        // 不考虑极端情况，最大性能是采用最大序列号除以机器数，机器数最好是双数.
        final int index = lastLength - 1;
        final int maxLimit = LIMIT_ARRAY[index];
        if (machineNum == 0 || machineNum == 1) {
            holder.setLimit(maxLimit);
            holder.getCurr().set(0);
            return;
        }
        int stepSize = maxLimit / machineNum;
        int nextStepLimit = jedisClient.incrBy(redisKey, stepSize).intValue();
        final String lockKey = LOCK_KEY_PREFIX.concat(bizKey);
        // 如果递增后超过了最大数，从0开始重新获取
        if (nextStepLimit > maxLimit || nextStepLimit < 0) {
            while (true) {
                if (jedisClient.setNxEX(lockKey, "lock", JedisEnums.NxxxEnum.NX.name(), JedisEnums.EXPX.EX.name(), 1)) {
                    nextStepLimit = jedisClient.incrBy(redisKey, stepSize).intValue();
                    if (nextStepLimit <= maxLimit && nextStepLimit >= stepSize) {
                        break;
                    }

                    jedisClient.set(redisKey, "0");
                    try {
                        jedisClient.expireAt(redisKey, DateUtils.addDays(DateUtils.parseDate(timeStr.substring(0, 8), "yyyyMMdd"), 1));
                    } catch (ParseException e) {
                        log.error("日期转换失败", e);
                        throw new RuntimeException();
                    }
                    nextStepLimit = jedisClient.incrBy(redisKey, stepSize).intValue();
                    jedisClient.remove(lockKey);
                    break;
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        log.warn("JedisSerialNo sleep 被中断", e);
                    }
                }
            }
        }

        holder.setLimit(nextStepLimit);
        holder.getCurr().set(nextStepLimit - stepSize);
    }

    @Data
    private static class NumHolder {

        /**
         * 当前数字.
         */
        private AtomicInteger curr;

        /**
         * 上限.
         */
        private int limit;

        private int getLimit() {
            return limit;
        }

        private void setLimit(int limit) {
            this.limit = limit;
        }
    }

}
