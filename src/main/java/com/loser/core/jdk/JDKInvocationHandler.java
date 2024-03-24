package com.loser.core.jdk;

import com.loser.util.JDKProxyUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * jdk 方法拦截
 *
 * @author loser
 */
public class JDKInvocationHandler implements InvocationHandler {

    public JDKInvocationHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isObjectMethod(method)) {
            return method.invoke(proxy, args);
        }
        JDKProxyWrapper wrapper = JDKProxyUtils.getHandlerByClass(method.getDeclaringClass());
        return wrapper.getProxyHandler().invoke(wrapper.getAnnotation(), proxy, method, args);
    }

    private boolean isObjectMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }
}