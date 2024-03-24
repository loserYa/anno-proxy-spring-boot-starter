package com.loser.core.cglib;

import java.lang.annotation.Annotation;
import java.util.Set;


/**
 * 注解线程标记
 *
 * @author loser
 */
public class AnnotationHolder {

    private static final ThreadLocal<Set<Annotation>> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Set<Annotation> annotations) {
        THREAD_LOCAL.set(annotations);
    }

    public static Set<Annotation> get() {
        return THREAD_LOCAL.get();
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

}
