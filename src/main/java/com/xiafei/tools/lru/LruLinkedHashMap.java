package com.xiafei.tools.lru;

import com.xiafei.tools.common.DateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <P>Description: 基于LinkedHashMap实现Lru算法（未实现线程安全）. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/6/27</P>
 * <P>UPDATE DATE: 2018/6/27</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class LruLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * 缓存容量，当linkedHashMap的容量超过这个时放弃最少使用的缓存.
     */
    private int capacity;
    /**
     * 为这个缓存起个名字，打印日志用，若未定义则采用初始化时间做名字.
     */
    private String name = "未定义" + DateUtils.getYMDHMSWithSeparate().format(new Date());

    /**
     * 初始化一个lru缓存，指定缓存大小.
     *
     * @param capacity 缓存大小
     */
    public LruLinkedHashMap(final int capacity) {
        super(capacity > 16 ? capacity >>> 1 : 16, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * 初始化一个lru缓存，指定缓存大小和名字.
     *
     * @param capacity 缓存大小
     */
    public LruLinkedHashMap(final int capacity, final String name) {
        super(capacity > 16 ? capacity >>> 1 : 16, 0.75f, true);
        this.capacity = capacity;
        this.name = name;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > capacity) {
            log.info("LRU[{}]移除最少使用节点,key={},value={}", name, eldest.getKey(), eldest.getValue());
            return size() > capacity;
        }
        return false;
    }


    public static void main(String[] args) {
        LruLinkedHashMap<String, String> lru = new LruLinkedHashMap(4, "小试牛刀");
        lru.put("1", "第一个节点");
        lru.put("2", "第二个节点");
        lru.put("3", "第三个节点");
        lru.put("4", "第四个节点");
        lru.put("5", "第五个节点");
        lru.put("6", "第六个节点");
        lru.get("3");
        lru.put("7", "第七个节点");

    }
}
