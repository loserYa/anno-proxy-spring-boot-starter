package com.loser.util;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * spring aop 工具类
 *
 * @author loser
 */
public class SpringProxyUtils {

    private SpringProxyUtils() {
    }

    public static AdvisedSupport getAdvisedSupport(Object proxy) throws Exception {
        Field h;
        if (Proxy.isProxyClass(proxy.getClass())) {
            h = proxy.getClass().getSuperclass().getDeclaredField("h");
        } else {
            h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        }
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return (AdvisedSupport) advised.get(dynamicAdvisedInterceptor);
    }

    public static boolean isProxy(Object bean) {
        if (bean == null) {
            return false;
        }
        return (Proxy.class.isAssignableFrom(bean.getClass()) || AopUtils.isAopProxy(bean));
    }

}
