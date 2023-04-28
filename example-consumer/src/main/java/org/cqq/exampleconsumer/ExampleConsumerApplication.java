package org.cqq.exampleconsumer;


import org.cqq.cqqrpc.framework.container.spring.RemotingScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RemotingScan("org.cqq.**.interfaces")
public class ExampleConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleConsumerApplication.class, args);
    }

}