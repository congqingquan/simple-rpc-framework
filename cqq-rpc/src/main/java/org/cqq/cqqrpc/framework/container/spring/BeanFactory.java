package org.cqq.cqqrpc.framework.container.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by QQ.Cong on 2023-04-28 / 9:26
 *
 * @Description Spring bean factory
 */
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanFactory.applicationContext = applicationContext;
    }

    /**
     * ==================== Basic function ====================
     */

    /**
     * 根据BeanName获取Bean
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据BeanName获取Bean
     * @param requiredType
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 根据BeanName与Type获取Bean
     * @param name
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 判断是否包含Bean
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 判断Bean是否为单例
     * @param name
     * @return
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * 判断Bean是否为多例
     * @param name
     * @return
     */
    public static boolean isPrototype(String name) {
        return applicationContext.isPrototype(name);
    }

    /**
     * 获取上下文资源
     * @param location
     * @return
     */
    public static Resource getResource(String location) {
        return applicationContext.getResource(location);
    }

    /**
     * 获取标记了指定注解的Bean列表
     * @param annotationClass
     * @return
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationClass) {
        return applicationContext.getBeansWithAnnotation(annotationClass);
    }

    /**
     * 获取指定类型的Bean列表
     * @param typeClass
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> typeClass) {
        return applicationContext.getBeansOfType(typeClass);
    }

    /**
     * 根据BeanName获取Bean的类型
     * @param name
     * @return
     */
    public static Class<? extends Object> getType(String name) {
        return applicationContext.getType(name);
    }

}