package com.loser.util;

import com.loser.core.jdk.JDKProxyWrapper;
import com.loser.core.jdk.JdkProxyHandler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdk代理类工具
 *
 * @author loser
 */
public class JDKProxyUtils {

    private static final Map<Class<?>, Class<?>> SERVICE_ANNO_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, JdkProxyHandler<?>> ANNO_HANDLER_MAP = new ConcurrentHashMap<>();

    public static void register(Class<?> serviceClass, Class<?> annoClass) {
        SERVICE_ANNO_MAP.put(serviceClass, annoClass);
    }

    public static Collection<Class<?>> listServiceClasses() {
        return SERVICE_ANNO_MAP.keySet();
    }

    public static void registerHandler(Class<?> annoClass, JdkProxyHandler<?> jdkProxyHandler) {
        ANNO_HANDLER_MAP.put(annoClass, jdkProxyHandler);
    }

    public static JDKProxyWrapper getHandlerByClass(Class<?> serviceClass) {
        Class annoClass = SERVICE_ANNO_MAP.get(serviceClass);
        return new JDKProxyWrapper(ANNO_HANDLER_MAP.get(SERVICE_ANNO_MAP.get(serviceClass)), serviceClass.getAnnotation(annoClass));
    }

}
