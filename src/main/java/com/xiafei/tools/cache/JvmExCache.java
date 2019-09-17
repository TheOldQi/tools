package com.xiafei.tools.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <P>Description: Jvm超时类缓存，用的时候自己new. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/12/29</P>
 * <P>UPDATE DATE: 2017/12/29</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class JvmExCache<D> {

    /**
     * 声明读写锁.
     */
    private static ReentrantReadWriteLock RW;

    /**
     * 读锁.
     */
    private static Lock R;

    /**
     * 写锁.
     */
    private static Lock W;

    /**
     * 是否允许脏读，若为是，当缓存达到过期时间后，挑选一个线程更新数据，其他线程读取更新前的数据直接返回.
     * 若为否，说明缓存达到过期时间后，所有后进入线程均需等待缓存更新后读取，
     * ###设为false使用cas原子操作，可以提升效率##
     */
    private boolean dirtyRead;

    /**
     * 如果允许脏读，用这个字段标识是否已经有线程拿到了更新权限.
     */
    private AtomicInteger updateFlag = new AtomicInteger(0);

    /**
     * 缓存失效的时间点.
     */
    private volatile long exAt;

    /**
     * 缓存失效间隔.
     */
    private long exIntervalMillis;

    /**
     * 缓存数据.
     */
    private volatile D data;

    /**
     * 更新数据任务.
     */
    private Task<D> task;

    /**
     * 初始化缓存对象，使用默认，非公平锁.
     *
     * @param exIntervalMillis 超时时间
     * @param task             能够获得缓存中数据的接口实现
     */
    public JvmExCache(final long exIntervalMillis, final Task<D> task) {
        RW = new ReentrantReadWriteLock();
        R = RW.readLock();
        W = RW.writeLock();
        dirtyRead = true;
        this.task = task;
        this.exIntervalMillis = exIntervalMillis;
    }

    /**
     * 初始化缓存对象，指定是否公平锁，非公平锁可以提升效率.
     *
     * @param exIntervalMillis 超时时间
     * @param task             能够获得缓存中数据的接口实现
     * @param dirtyRead        是否允许脏读
     * @param isFair           是否公平锁s
     */
    public JvmExCache(final long exIntervalMillis, final Task<D> task, final boolean dirtyRead, final boolean isFair) {
        RW = new ReentrantReadWriteLock(isFair);
        R = RW.readLock();
        W = RW.writeLock();
        this.dirtyRead = dirtyRead;
        this.task = task;
        this.exIntervalMillis = exIntervalMillis;
    }

    /**
     * 从缓存中取数据，方法会自动判断如果过期的话刷新数据.
     *
     * @return 缓存中的数据.
     * @throws Exception task可能抛出的异常
     */
    public D getAndRefreshIfEx()  {
        if (data == null) {
            return refreshData();
        }
        if (dirtyRead) {
            if (exAt < System.currentTimeMillis() && updateFlag.compareAndSet(0, 1)) {
                data = task.invoke();
                exAt = System.currentTimeMillis() + exIntervalMillis;
                updateFlag.set(0);
                return data;
            } else {
                return data;
            }

        } else {
            if (exAt < System.currentTimeMillis()) {
                return refreshData();
            } else {
                try {
                    R.lock();
                    return data;
                } finally {
                    R.unlock();
                }
            }
        }

    }

    private D refreshData()  {
        if (W.tryLock()) {
            try {
                data = task.invoke();
                exAt = System.currentTimeMillis() + exIntervalMillis;
                return data;
            } finally {
                W.unlock();
            }
        } else {
            R.lock();
            try {
                return data;
            } finally {
                R.unlock();
            }
        }
    }


    /**
     * 内部接口.
     *
     * @param <D> 接口方法返回数据类型泛型
     */
    public interface Task<D> {

        /**
         * 调用该方法可以返回缓存数据对象.
         *
         * @return 缓存的数据对象
         */
        D invoke() ;
    }
}
