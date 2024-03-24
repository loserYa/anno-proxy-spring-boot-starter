package com.loser.core.cglib;

import com.loser.util.CglibProxyUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * cglib 方法拦截
 *
 * @author loser
 */
@SuppressWarnings("all")
public class CglibMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        try {
            Annotation[] annotations = methodInvocation.getMethod().getAnnotations();
            if (Objects.isNull(annotations) || annotations.length == 0) {
                return methodInvocation.proceed();
            }
            CglibProxyWrapper cglibProxyWrapper = CglibProxyUtils.getProxyHandler(annotations);
            if (Objects.isNull(cglibProxyWrapper)) {
                return methodInvocation.proceed();
            }
            return cglibProxyWrapper.getProxyHandler().invoke(cglibProxyWrapper.getAnnotation(), methodInvocation);
        } finally {
            AnnotationHolder.clear();
        }

    }

}
