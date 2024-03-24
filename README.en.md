# anno-proxy-spring-boot-starter

#### 介绍

自定义注解快速生成代理类

#### 使用说明

1. 实现默认接口 CglibProxyHandler 并注入容器 可快速 对普通bean进行cglig子类代理
2. 实现默认接口 JdkProxyHandler 并注入容器 可快速 对接口进行jdk动态代理并放入容器
