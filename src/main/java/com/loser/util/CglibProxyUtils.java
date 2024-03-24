package com.loser.util;

import com.loser.core.cglib.AnnotationHolder;
import com.loser.core.cglib.CglibProxyHandler;
import com.loser.core.cglib.CglibProxyWrapper;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cglib代理类工具
 *
 * @author loser
 */
public class CglibProxyUtils {

    private static final Map<Class<?>, CglibProxyHandler<? extends Annotation>> ANNO_HANDLER_MAP = new ConcurrentHashMap<>();

    private static final Map<String, CglibProxyHandler<? extends Annotation>> ANNO_NAME_HANDLER_MAP = new ConcurrentHashMap<>();

    public static Set<Class<?>> listAnnoClasses() {
        return ANNO_HANDLER_MAP.keySet();
    }

    public static void register(Class<?> annoClass, CglibProxyHandler<? extends Annotation> cglibProxyHandler) {
        ANNO_HANDLER_MAP.put(annoClass, cglibProxyHandler);
        ANNO_NAME_HANDLER_MAP.put(annoClass.getName(), cglibProxyHandler);
    }

    public static CglibProxyWrapper getProxyHandler(Annotation[] annotations) {

        Set<Annotation> list = AnnotationHolder.get();
        for (int length = annotations.length; length > 0; length--) {
            Annotation annotation = annotations[length - 1];
            if (list != null && list.size() > 0 && list.contains(annotation)) {
                continue;
            }
            String name = annotation.toString().replace("@", "").split("\\(")[0];
            CglibProxyHandler<? extends Annotation> cglibProxyHandler = ANNO_NAME_HANDLER_MAP.get(name);
            if (Objects.nonNull(cglibProxyHandler)) {
                if (list == null || list.size() == 0) {
                    list = new HashSet<>();
                }
                list.add(annotation);
                AnnotationHolder.set(list);
                return new CglibProxyWrapper(ANNO_NAME_HANDLER_MAP.get(name), annotation);
            }
        }
        return null;

    }


}
