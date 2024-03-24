package com.loser.config;

import com.loser.core.cglib.AnnoCglibProxyCreator;
import com.loser.core.cglib.CglibProxyHandler;
import com.loser.core.jdk.AnnoJDKProxyCreator;
import com.loser.core.jdk.JDKInvocationHandler;
import com.loser.core.jdk.JdkProxyHandler;
import com.loser.core.jdk.JdkScannerHandler;
import com.loser.util.AnnotationScanner;
import com.loser.util.CglibProxyUtils;
import com.loser.util.JDKProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 配置类
 *
 * @author loser
 */
@SuppressWarnings("all")
public class ProxyConfiguration {

    /**
     * JDK处理器
     *
     * @return 处理器
     */
    @Bean
    public JDKInvocationHandler jdkInvocationHandler() {
        synchronized (ProxyConfiguration.class) {
            return new JDKInvocationHandler();
        }
    }

    /**
     * JDK接口扫描处理器
     *
     * @return 处理器
     */
    @Bean
    public JdkScannerHandler jdkScannerHandler(ConfigurableListableBeanFactory factory) {
        return new JdkScannerHandler(factory);
    }

    /**
     * cglib 处理器
     *
     * @param cglibProxyHandlers 业务处理器集合
     * @return cglib处理器
     */
    @Bean
    @DependsOn("jdkInvocationHandler")
    public AnnoCglibProxyCreator annoCglibProxyCreator(@Autowired(required = false) List<CglibProxyHandler> cglibProxyHandlers) {

        synchronized (ProxyConfiguration.class) {
            // 注册自定义注解和处理器的映射关系
            if (!CollectionUtils.isEmpty(cglibProxyHandlers)) {
                for (CglibProxyHandler cglibProxyHandler : cglibProxyHandlers) {
                    CglibProxyUtils.register(getTClass(cglibProxyHandler, CglibProxyHandler.class), cglibProxyHandler);
                }
            }
            return new AnnoCglibProxyCreator();
        }

    }

    /**
     * JDK处理器
     *
     * @return 处理器
     */
    @Bean
    @DependsOn({"annoCglibProxyCreator", "jdkScannerHandler"})
    public AnnoJDKProxyCreator annoJDKProxyCreator(@Autowired(required = false) List<JdkProxyHandler> jdkProxyHandlers, JdkScannerHandler jdkScannerHandler, AnnotationConfigApplicationContext applicationContext) {

        synchronized (ProxyConfiguration.class) {
            List<String> packageNames = jdkScannerHandler.getPackageNames();
            // 注册接口和自定义注解的映射关系 注册自定义注解和处理器的映射关系
            if (!CollectionUtils.isEmpty(jdkProxyHandlers)) {
                List<? extends Class<? extends Annotation>> targets = jdkProxyHandlers.stream().map(jdkProxyHandler -> {
                    return (Class<? extends Annotation>) getTClass(jdkProxyHandler, JdkProxyHandler.class);
                }).collect(Collectors.toList());
                Set<Class<?>> classes = AnnotationScanner.scanPackages(packageNames, targets, applicationContext);
                for (JdkProxyHandler jdkProxyHandler : jdkProxyHandlers) {
                    Class<? extends Annotation> bizAnno = (Class<? extends Annotation>) getTClass(jdkProxyHandler, JdkProxyHandler.class);
                    List<Class<?>> classList = classes.stream().filter(item -> item.isAnnotationPresent(bizAnno)).collect(Collectors.toList());
                    for (Class<?> bizService : classList) {
                        JDKProxyUtils.register(bizService, bizAnno);
                    }
                    JDKProxyUtils.registerHandler(bizAnno, jdkProxyHandler);
                }
            }
            return new AnnoJDKProxyCreator();
        }

    }

    /**
     * 获取泛型
     */
    private Class<?> getTClass(Object proxyHandler, Class<?> target) {

        if (Objects.isNull(proxyHandler)) {
            return null;
        }
        Type[] supperClass = proxyHandler.getClass().getGenericInterfaces();
        for (Type aClass : supperClass) {
            if (aClass instanceof ParameterizedTypeImpl) {
                if (((ParameterizedTypeImpl) aClass).getRawType() != target) {
                    continue;
                }
                Type subType = ((ParameterizedType) aClass).getActualTypeArguments()[0];
                return (Class<?>) subType;
            }
        }
        return null;

    }

}
