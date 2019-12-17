package com.xiafei.tools;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <P>Description: 跑单元测试时候，可以使用这个类加快测试速度. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2018/1/19</P>
 * <P>UPDATE DATE: 2018/1/19</P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class TestUtils {


    /**
     * 给对象设置一些初始属性.
     *
     * @param object 要改变属性的对象
     * @param serial 方便保证唯一性，允许传入一个序列号
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> T setProperties(T object, Long serial) throws IllegalAccessException {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (java.lang.reflect.Modifier.isFinal(f.getModifiers())) {
                System.out.println("final对象字段忽略" + f.getName());
                continue;
            }
            Class valCls = f.getType();
            if (valCls == String.class) {
                f.set(object, "测试" + serial);
            } else if (Byte.class == valCls) {
                f.set(object, (byte) 1);
            } else if (Short.class == valCls) {
                f.set(object, (short) 1);
            } else if (Integer.class == valCls) {
                f.set(object, 1);
            } else if (Long.class == valCls) {
                f.set(object, 1L);
            } else if (Float.class == valCls) {
                f.set(object, 1.0F);
            } else if (Double.class == valCls) {
                f.set(object, 1.0);
            } else if (Character.class == valCls) {
                f.set(object, 'A');
            } else if (Boolean.class == valCls) {
                f.set(object, true);
            } else if (Date.class == valCls) {
                f.set(object, new Date());
            } else if (BigDecimal.class == valCls) {
                f.set(object, BigDecimal.ONE);
            } else {
                System.out.println("引用类型对象字段忽略" + f.getName());
            }
        }
        return object;
    }

    public static void main(String[] args) throws IllegalAccessException, InterruptedException {
//
//        List<byte[]> oom = new ArrayList<>();
//        try {
//
//            for (int i = 0; i < 1000; i++) {
//                oom.add(new byte[1024 * 1024]);
//            }
//        } catch (Throwable e) {
//            System.out.println("捕获到异常了");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    List<byte[]> oom2 = new ArrayList<>();
//                    oom2.add(new byte[1024 * 1024]);
//                    System.out.println("另一个线程执行");
//                    System.out.println(oom2.toString());
//
//                }
//            }).start();
//            TimeUnit.SECONDS.sleep(5);
//        }
//        System.out.println("程序继续执行");
//
//        oom = null;
        t.set(2);
        System.gc();
        Thread.sleep(2000);
        System.out.println(t.get());
        Test test = new Test(129);
        System.gc();
        System.out.println(test.get());
    }

    private static ThreadLocal<Integer> t = ThreadLocal.withInitial(() -> 0);

    static class Test extends WeakReference<Integer> {

        public Test(final Integer referent) {
            super(referent);
        }
    }

}
