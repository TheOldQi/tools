package com.xiafei.tools.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <P>Description: 接口仿制插件，请将该类放在test目录下并保证可以被spring容器扫描. </P>
 * <P>将仿制接口放在该类目录下一级MOCKED_PACKAGE_NAME指定的包下</P>
 * <P>原理是扫描指定包路径下所有类，替换spring容器中实现相同接口的bean为仿制类</P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   </P>
 * <P>CREATE DATE: 2019/6/24 10:39</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Component
public class MockPlugin implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(MockPlugin.class);

    /**
     * 已经仿制的接口.
     */
    private static final Map<Class, Object  /* mockBean */> MOCKED_MAP = new HashMap<>();


    /**
     * 跑单元测试beforeClass块调用该方法.
     *
     * @param mockPackage 本单元测试类指定mock类包相对MockPlugin的路径
     */
    public static void refreshMock(final String mockPackage) {
        try {
            initMockedMap(mockPackage);
        } catch (Exception e) {
            log.error("mock类初始化失败", e);
        }
    }


    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        final Class beanClass = bean.getClass();
        final Class[] interfaces = beanClass.getInterfaces();
        for (Class c : interfaces) {
            Object o;
            if ((o = MOCKED_MAP.get(c)) != null) {
                return o;
            }
        }
        return bean;
    }

    /**
     * @param mockPackage mock类相对MockPlugin类package路径，例:.mocked
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static void initMockedMap(final String mockPackage) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (mockPackage == null || mockPackage.trim().equals("")) {
            MOCKED_MAP.clear();
            return;
        }
        String packageName = MockPlugin.class.getPackage().getName() + mockPackage;

        addMock(packageName);
    }

    private static void addMock(final String packageName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final String packagePath = getPackagePath(packageName);
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (resource == null) {
            return;
        }
        final String packageFilePath = resource.getFile();
        final File packageDirectory = new File(packageFilePath);
        String[] fileNames = packageDirectory.list();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".class")) {

                Class<?> aClass = Class.forName(packageName + "." + fileName.substring(0, fileName.indexOf(".class")));
                Class<?>[] interfaces = aClass.getInterfaces();
                if (interfaces.length != 1) {
                    log.error("mock类只能实现要mock的接口，请勿多实现其他接口，仿制类[{}]加载失败", fileName);
                    continue;
                }
                Object o = aClass.newInstance();
                MOCKED_MAP.put(interfaces[0], o);

            }
        }
    }

    private static String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }


}
