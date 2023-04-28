package org.cqq.cqqrpc.framework.container.spring;

import org.cqq.cqqrpc.framework.proxy.jdk.RemotingProxyInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Created by QQ.Cong on 2023-04-25 / 13:28
 *
 * @Description 远程调用代理对象工厂 Bean
 */
public class RemotingProxyFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> interfaces;

    public RemotingProxyFactoryBean(Class<T> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getObject() {
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(), new Class[]{interfaces}, new RemotingProxyInvocationHandler<>(interfaces));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaces;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}