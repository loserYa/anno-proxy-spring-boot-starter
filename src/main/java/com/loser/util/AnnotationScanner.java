package com.loser.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 注解扫描工具
 *
 * @author loser
 */
public class AnnotationScanner {

    public static Set<Class<?>> scanPackages(List<String> packageNamePatterns, List<? extends Class<? extends Annotation>> annotations, AnnotationConfigApplicationContext applicationContext) {

        Set<Class<?>> annotatedInterfaces = new HashSet<>();
        for (String packageNamePattern : packageNamePatterns) {
            Set<Class<?>> list = scanPackage(packageNamePattern, annotations, applicationContext);
            if (!CollectionUtils.isEmpty(list)) {
                annotatedInterfaces.addAll(list);
            }
        }
        return annotatedInterfaces;

    }

    public static Set<Class<?>> scanPackage(String packageName, List<? extends Class<? extends Annotation>> annotations, AnnotationConfigApplicationContext applicationContext) {

        ProxyClassPathScanningCandidateComponentProvider scanner = new ProxyClassPathScanningCandidateComponentProvider(applicationContext);
        return scanner.scanner(packageName, annotations);

    }

    public static class ProxyClassPathScanningCandidateComponentProvider extends ClassPathScanningCandidateComponentProvider {

        private final ResourcePatternResolver resourcePatternResolver;
        private final MetadataReaderFactory metadataReaderFactory;


        public ProxyClassPathScanningCandidateComponentProvider(AnnotationConfigApplicationContext applicationContext) {
            this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(applicationContext);
            this.metadataReaderFactory = new CachingMetadataReaderFactory(applicationContext);
        }

        @Override
        protected String resolveBasePackage(String basePackage) {
            Environment environment = this.getEnvironment();
            if (Objects.isNull(environment)) {
                environment = new StandardEnvironment();
            }
            return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
        }

        public Set<Class<?>> scanner(String basePackage, List<? extends Class<? extends Annotation>> annotations) {

            Set<Class<?>> candidates = new LinkedHashSet<>();
            try {
                String packageSearchPath = "classpath*:" + this.resolveBasePackage(basePackage) + "/**/*.class";
                Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        try {
                            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                            Class<?> match = match(metadataReader, annotations);
                            if (Objects.nonNull(match)) {
                                candidates.add(match);
                            }
                        } catch (Throwable e) {
                            throw new RuntimeException("Failed to read candidate component class: " + resource, e);
                        }
                    }
                }
                return candidates;
            } catch (IOException e) {
                throw new RuntimeException("I/O failure during classpath scanning", e);
            }
        }

    }

    private static Class<?> match(MetadataReader metadataReader, List<? extends Class<? extends Annotation>> annotations) {

        if (!metadataReader.getClassMetadata().isInterface()) {
            return null;
        }
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        try {
            Class<?> aClass = Class.forName(classMetadata.getClassName());
            for (Class<? extends Annotation> annotation : annotations) {
                if (aClass.isAnnotationPresent(annotation)) {
                    return aClass;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;

    }

}
