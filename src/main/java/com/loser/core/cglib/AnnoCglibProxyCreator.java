package com.loser.core.cglib;

import com.loser.util.CglibProxyUtils;
import com.loser.util.SpringProxyUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 使用cglib添加代理或者添加切面
 *
 * @author loser
 */
public class AnnoCglibProxyCreator extends AbstractAutoProxyCreator {

    /**
     * 拦截器
     */
    private static final MethodInterceptor interceptor = new CglibMethodInterceptor();

    /**
     * 获取bean切面
     */
    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String s, TargetSource targetSource) throws BeansException {
        return new Object[]{interceptor};
    }

    /**
     * jdk动态代理切面添加
     *
     * @return 添加后端额bane
     */
    public Object wrapJdkRegister(Object bean, String beanName, Class<?> targetClass) {

        try {
            for (Class<?> set : CglibProxyUtils.listAnnoClasses()) {
                if (findAnnoByType(targetClass, set)) {
                    AdvisedSupport advised = SpringProxyUtils.getAdvisedSupport(bean);
                    Advisor[] advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(null, null, null));
                    for (Advisor avr : advisor) {
                        advised.addAdvisor(avr);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("add advisor error", e);
        }
        return bean;

    }

    /**
     * 添加切面
     */
    @Override
    public Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {

        try {
            for (Class<?> set : CglibProxyUtils.listAnnoClasses()) {
                if (findAnnoByType(bean.getClass(), set)) {
                    if (SpringProxyUtils.isProxy(bean)) {
                        AdvisedSupport advised = SpringProxyUtils.getAdvisedSupport(bean);
                        Advisor[] advisor = buildAdvisors(beanName, getAdvicesAndAdvisorsForBean(null, null, null));
                        for (Advisor avr : advisor) {
                            advised.addAdvisor(avr);
                        }
                    } else {
                        bean = super.wrapIfNecessary(bean, beanName, cacheKey);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("add advisor error", e);
        }
        return bean;

    }

    /**
     * 查询指定注解
     */
    public static boolean findAnnoByType(Class<?> clazz, Class target) {

        if (Objects.isNull(clazz)) {
            return false;
        }
        if (clazz.equals(Object.class)) {
            return false;
        }
        Object annotation;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            annotation = method.getAnnotation(target);
            if (Objects.nonNull(annotation)) {
                return true;
            }
        }
        return findAnnoByType(clazz.getSuperclass(), target);

    }

}
