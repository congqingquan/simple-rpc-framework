package org.cqq.cqqrpc.autoconfigure;

import org.cqq.cqqrpc.framework.container.spring.BeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by QQ.Cong on 2023-04-28 / 11:46
 *
 * @Description cqq-rpc spring boot stater auto config
 */
@Configuration
public class C99RPCAutoConfig {

    @Bean
    @ConfigurationProperties("cqq-rpc")
    public C99RPCSettings c99RPCSettings() {
        return new C99RPCSettings();
    }

    @Bean
    public C99RPCApplicationStartListener c99RPCApplicationListener(C99RPCSettings settings) {
        return new C99RPCApplicationStartListener(settings);
    }

    @Bean
    public BeanFactory c99RPCBeanFactory(ApplicationContext applicationContext) {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.setApplicationContext(applicationContext);
        return beanFactory;
    }
}