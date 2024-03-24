package com.loser.core.jdk;

import com.loser.core.cglib.AnnoCglibProxyCreator;
import com.loser.util.CamelCaseConverter;
import com.loser.util.JDKProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用jdk动态代理添加代理类
 *
 * @author loser
 */
public class AnnoJDKProxyCreator implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final Map<Class<?>, Object> PROXY_OBJ_MAP = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        JDKInvocationHandler invocationHandler = applicationContext.getBean(JDKInvocationHandler.class);
        Collection<Class<?>> classes = JDKProxyUtils.listServiceClasses();
        for (Class<?> aClass : classes) {
            Object proxy = Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{aClass}, invocationHandler);
            Class<?> proxyClass = proxy.getClass();
            // 创建一个新的 bean 定义
            BeanDefinition beanDefinition = BeanDefinitionBuilder
                    .genericBeanDefinition(proxyClass)
                    .getBeanDefinition();
            String beanName = CamelCaseConverter.toLowerCamelCase(aClass.getSimpleName());
            beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);

            // 创建代理工厂
            ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.setTarget(proxy);
            Object springProxy = proxyFactory.getProxy();
            PROXY_OBJ_MAP.put(aClass, springProxy);

        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

        AnnoCglibProxyCreator annoCglibProxyCreator = applicationContext.getBean(AnnoCglibProxyCreator.class);
        for (Map.Entry<Class<?>, Object> entry : PROXY_OBJ_MAP.entrySet()) {
            Class<?> bizClass = entry.getKey();
            Object target = entry.getValue();
            String beanName = CamelCaseConverter.toLowerCamelCase(bizClass.getSimpleName());
            target = annoCglibProxyCreator.wrapJdkRegister(target, beanName, bizClass);
            configurableListableBeanFactory.registerSingleton(beanName, target);
        }

    }

}
