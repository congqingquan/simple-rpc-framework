package org.cqq.cqqrpc.framework.container.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by QQ.Cong on 2023-04-26 / 11:33:16
 *
 * @Description Remoting scan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RemotingProxyBeanDefinitionRegistry.class})
public @interface RemotingScan {
 
    String[] value() default {};
 
    String[] basePackages() default {};
 
}