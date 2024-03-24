package com.loser.core.cglib;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;

/**
 * 给添加自定义注解的方法添 cglib 切面
 *
 * @author loser
 */
public interface CglibProxyHandler<T extends Annotation> {

    Object invoke(T annotation, MethodInvocation methodInvocation) throws Throwable;

}
