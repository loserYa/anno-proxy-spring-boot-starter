package com.loser.core.jdk;


import java.lang.annotation.Annotation;

/**
 * jdk处理类包裹器
 *
 * @author loser
 */
public class JDKProxyWrapper {

    private JdkProxyHandler proxyHandler;

    private Annotation annotation;

    public JDKProxyWrapper(JdkProxyHandler proxyHandler, Annotation annotation) {
        this.proxyHandler = proxyHandler;
        this.annotation = annotation;
    }

    public JdkProxyHandler getProxyHandler() {
        return proxyHandler;
    }

    public void setProxyHandler(JdkProxyHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }
}
