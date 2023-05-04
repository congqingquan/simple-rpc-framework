package org.cqq.exampleconsumer;

import org.cqq.cqqrpc.framework.netty.RPCNettyClient;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;
import org.cqq.exampleinterface.interfaces.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @RequestMapping("/test")
    public String test(String msg) {
        return testService.test(msg);
    }
    
    public static void main(String[] args) throws Exception {
        RPCNettyClient rpcNettyClient = RPCNettyClient.getInstance();
        Thread.sleep(1000);
    
//        RPCRequestMessage rpcRequestMessage = new RPCRequestMessage();
//        rpcRequestMessage.setInterfaceName(TestService.class.getName());
//        rpcRequestMessage.setMethodName("test");
//        rpcRequestMessage.setParameterTypes(new Class<?>[]{String.class});
//        rpcRequestMessage.setParameterValue(new Object[]{String.valueOf(1)});
//        rpcRequestMessage.setSequenceId(String.valueOf(1));
//        Object response = rpcNettyClient.request(rpcRequestMessage);

        int c = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(c);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < c; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                RPCRequestMessage rpcRequestMessage = new RPCRequestMessage();
                rpcRequestMessage.setInterfaceName(TestService.class.getName());
                rpcRequestMessage.setMethodName("test");
                rpcRequestMessage.setParameterTypes(new Class<?>[]{String.class});
                rpcRequestMessage.setParameterValue(new Object[]{String.valueOf(finalI)});
                rpcRequestMessage.setSequenceId(String.valueOf(finalI));
                Object response = rpcNettyClient.request(rpcRequestMessage);
                String idx = response.toString().split(" ")[1];
                if (!Integer.valueOf(idx).equals(finalI)) {
                    System.err.println("!!!!!!!");
                    System.exit(0);
                }
                System.out.println(Thread.currentThread().getName() + ": " + idx + "(" + response + ")");
            }, String.valueOf(finalI));
            threads.add(thread);
            countDownLatch.countDown();
        }
        countDownLatch.await();
        threads.forEach(Thread::start);
    }
}