package org.cqq.cqqrpc.framework.container.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by QQ.Cong on 2023-04-25 / 16:55
 *
 * @Description 注册: 标记了 Remoting 注解的接口扫描器
 */
public class RemotingProxyBeanDefinitionRegistry implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取 RemotingScan 注解
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RemotingScan.class.getName()));
        if (annotationAttributes == null) {
            return;
        }
        // 获取扫描路径
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(collectPackages(annotationAttributes, "value"));
        basePackages.addAll(collectPackages(annotationAttributes, "basePackages"));

        // 根据路径扫描标记了指定注解的接口，并生成注册 BeanDefinition
        RemotingAnnotatedInterfacesScanner classPathScanner = new RemotingAnnotatedInterfacesScanner(beanDefinitionRegistry);
        classPathScanner.doScan(StringUtils.collectionToCommaDelimitedString(basePackages));
    }

    private List<String> collectPackages(AnnotationAttributes annotationAttributes, String attributeName) {
        return Arrays.stream(annotationAttributes.getStringArray(attributeName)).filter(StringUtils::hasText).collect(Collectors.toList());
    }
}