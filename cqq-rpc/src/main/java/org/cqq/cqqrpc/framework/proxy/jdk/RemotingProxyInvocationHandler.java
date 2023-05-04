package org.cqq.cqqrpc.framework.proxy.jdk;

import lombok.extern.slf4j.Slf4j;
import org.cqq.cqqrpc.framework.netty.RPCNettyClient;
import org.cqq.cqqrpc.framework.netty.message.Message;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;
import org.cqq.cqqrpc.framework.util.SequenceIdGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by QQ.Cong on 2023-04-26 / 9:51
 *
 * @Description Remoting 代理类的执行逻辑
 */
@Slf4j
public class RemotingProxyInvocationHandler<T> implements InvocationHandler {

    // 实际上，对于远程调用、生成 Mybatis Mapper 代理类这种情况，我们是不需要 target 实例的。
    // 知道调用了接口的哪个方法即可。比如 远程调用：根据调用的接口方法数据调用 provider，并将结果返回。又如 mapper 代理：找到对应的 xml 中的 Sql 并执行，将执行结果封装为接口方法的返回值。
    private final Class<T> target;

    public RemotingProxyInvocationHandler(Class<T> target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String interfaceName = target.getName();
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        Class<?>[] parameterTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        RPCRequestMessage request = new RPCRequestMessage();
        request.setType(Message.Type.RPC_REQUEST_MESSAGE);
        request.setInterfaceName(interfaceName);
        request.setMethodName(methodName);
        request.setReturnType(returnType);
        request.setParameterTypes(parameterTypes);
        request.setParameterValue(args);
        request.setSequenceId(SequenceIdGenerator.getSequenceId());
        log.info("RPC request [{}]", request);
        return RPCNettyClient.getInstance().request(request);
    }
}