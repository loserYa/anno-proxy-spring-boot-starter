package com.loser.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启注解功能
 *
 * @author loser
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ProxyConfiguration.class})
public @interface EnableAnnoProxy {

    /***
     * 包扫描路径
     */
    String[] basePackages() default "";

}
