package org.cqq.cqqrpc.framework.container.spring;

import org.cqq.cqqrpc.framework.common.annotation.Remoting;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;
import java.util.Set;

/**
 * Created by QQ.Cong on 2023-04-25 / 16:44:24
 *
 * @Description 标记了 Remoting 注解的接口扫描器
 */
public class RemotingAnnotatedInterfacesScanner extends ClassPathBeanDefinitionScanner {

    public RemotingAnnotatedInterfacesScanner(BeanDefinitionRegistry registry) {
        // false: 不使用 ClassPathBeanDefinitionScanner 默认的 TypeFilter (@Component)
        super(registry, false);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        // 设置扫描规则: 扫描所有类
        addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
        // 开始扫描
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            return beanDefinitionHolders;
        }
        // 扫描标记了 Remoting 注解的接口
        this.createBeanDefinition(beanDefinitionHolders);
        return beanDefinitionHolders;
    }

    /**
     * 扫描标记了 Remoting 注解的接口
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isInterface() && metadata.isIndependent() &&  metadata.getAnnotationTypes().contains(Remoting.class.getName());
    }

    /**
     * 为扫描到的接口创建代理对象
     */
    private void createBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            GenericBeanDefinition beanDefinition = ((GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition());
            // 将 bean 的真实类型改变为 FactoryBean
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(beanDefinition.getBeanClassName()));
            beanDefinition.setBeanClass(RemotingProxyFactoryBean.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        }
    }

}