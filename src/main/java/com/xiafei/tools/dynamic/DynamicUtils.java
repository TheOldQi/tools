package com.xiafei.tools.dynamic;

import com.xiafei.tools.lru.LruLinkedHashMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <P>Description: 动态语言支持工具. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2019/5/20 17:55</P>
 * <P>UPDATE DATE: </P>
 *
 * @author qixiafei
 * @version 1.0
 * @since java 1.8.0
 */
public class DynamicUtils {


    private static final Map<String, Map<String, Object>> POOL = new LruLinkedHashMap<>(10000);
    private static final String METHOD_KEY = "method";
    private static final String OBJECT_KEY = "object";
    private static final Object MUTEX = new Object();


    /**
     * 执行动态代码.
     *
     * @param code   代码
     * @param params 参数
     * @return 代码执行的返回值
     */
    public static <T> T execute(final String code, final Class<T> rt, Object... params) throws Exception {
        Map<String, Object> cacheMap;
        if ((cacheMap = POOL.get(code)) != null) {
            return (T) ((Method) cacheMap.get(METHOD_KEY)).invoke(cacheMap.get(OBJECT_KEY), params);
        }
        synchronized (MUTEX) {
            if ((cacheMap = POOL.get(code)) != null) {
                return (T) ((Method) cacheMap.get(METHOD_KEY)).invoke(cacheMap.get(OBJECT_KEY), params);
            }

            cacheMap = new HashMap<>(4);

            final ClassPool classPool = ClassPool.getDefault();
            final String className = "Dynamic" + UUID.randomUUID().toString().replace("-", "");
            final CtClass cc = classPool.makeClass(className);
            final CtMethod ctM = CtNewMethod.make(" public " + rt.getSimpleName() + " execute(" + getParamSign(params) + "){" + code + "}", cc);
            cc.addMethod(ctM);
            final Class aClass = cc.toClass();
            final Object o = aClass.newInstance();
            final Method m = aClass.getDeclaredMethods()[0];

            cacheMap.put(METHOD_KEY, m);
            cacheMap.put(OBJECT_KEY, o);
            POOL.put(code, cacheMap);

            return (T) m.invoke(o, params);
        }

    }

    /**
     * 测试动态代码.
     *
     * @param code   代码
     * @param params 参数
     */
    public static <T> void test(final String code, final Class<T> rt, Object... params) throws Exception {
        final ClassPool classPool = ClassPool.getDefault();
        final String className = "Dynamic" + UUID.randomUUID().toString().replace("-", "");
        final CtClass cc = classPool.makeClass(className);
        final CtMethod ctM = CtNewMethod.make(" public " + rt.getSimpleName() + " execute(" + getParamSign(params) + "){" + code + "}", cc);
        cc.addMethod(ctM);
        final Class aClass = cc.toClass();
        final Object o = aClass.newInstance();
    }


    private static String getParamSign(Object... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();

        for (int i = 0, len = params.length; i < len; i++) {
            sb.append(params[i].getClass().getSimpleName()).append(" $").append(i).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();

    }

}