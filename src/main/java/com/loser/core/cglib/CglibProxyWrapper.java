package com.loser.core.cglib;

import java.lang.annotation.Annotation;

/**
 * cglib 处理器包裹类
 *
 * @author loser
 */
public class CglibProxyWrapper {

    private CglibProxyHandler cglibProxyHandler;

    private Annotation annotation;

    public CglibProxyWrapper(CglibProxyHandler cglibProxyHandler, Annotation annotation) {
        this.cglibProxyHandler = cglibProxyHandler;
        this.annotation = annotation;
    }

    public CglibProxyHandler getProxyHandler() {
        return cglibProxyHandler;
    }

    public void setProxyHandler(CglibProxyHandler cglibProxyHandler) {
        this.cglibProxyHandler = cglibProxyHandler;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}
