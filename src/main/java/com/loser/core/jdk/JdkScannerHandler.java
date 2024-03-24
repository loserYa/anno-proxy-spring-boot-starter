package com.loser.core.jdk;

import com.loser.config.EnableAnnoProxy;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdkScannerHandler {

    private final List<String> packageNames = new ArrayList<>();

    public List<String> getPackageNames() {
        return packageNames;
    }

    public JdkScannerHandler(ConfigurableListableBeanFactory beanFactory) {

        try {
            beanFactory.getBeansWithAnnotation(EnableAnnoProxy.class).forEach((key, value) -> {
                EnableAnnoProxy proxy = value.getClass().getAnnotation(EnableAnnoProxy.class);
                for (String basePackage : proxy.basePackages()) {
                    if (Objects.nonNull(basePackage)) {
                        packageNames.add(basePackage);
                    }
                }
            });
        } catch (Exception ignore) {
            if (CollectionUtils.isEmpty(packageNames)) {
                packageNames.add("");
            }
        }

    }

}
