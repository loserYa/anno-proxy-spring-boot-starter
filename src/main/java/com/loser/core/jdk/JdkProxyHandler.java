package com.loser.core.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * jdk接口代理处理类
 *
 * @author loser
 */
public interface JdkProxyHandler<T extends Annotation> {

    Object invoke(T annotation, Object proxy, Method method, Object[] args);

}
