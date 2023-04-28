package org.cqq.cqqrpc.autoconfigure;

import org.cqq.cqqrpc.framework.container.spring.BeanFactory;
import org.cqq.cqqrpc.framework.netty.RPCNettyServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by QQ.Cong on 2023-04-28 / 11:46
 *
 * @Description cqq-rpc spring boot stater config
 */
@Configuration
@EnableConfigurationProperties(C99RPCSettings.class)
public class C99RPCAutoConfig implements InitializingBean {

    private final C99RPCSettings settings;

    public C99RPCAutoConfig(C99RPCSettings settings) {
        this.settings = settings;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RPCNettyServer server = new RPCNettyServer(settings.getServerName());
        server.start();
    }

    @Bean
    public BeanFactory cqqRPCBeanFactory(ApplicationContext applicationContext) {
        BeanFactory beanFactory = new BeanFactory();
        beanFactory.setApplicationContext(applicationContext);
        return beanFactory;
    }
}