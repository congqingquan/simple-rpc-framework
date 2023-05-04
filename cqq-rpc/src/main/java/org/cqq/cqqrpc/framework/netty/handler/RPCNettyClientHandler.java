package org.cqq.cqqrpc.framework.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;
import org.cqq.cqqrpc.framework.netty.message.RPCResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by QQ.Cong on 2023-04-28 / 15:54
 *
 * @Description PRC netty client handler
 */
@ChannelHandler.Sharable
public class RPCNettyClientHandler extends ChannelInboundHandlerAdapter {

    @Data
    private static class Response {
        
        private Thread thread;
        
        private RPCResponseMessage responseMessage;
    }
    
    private ChannelHandlerContext context;
    
    private final Map<String, Response> requests = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }
    
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RPCResponseMessage) {
            RPCResponseMessage responseMessage = (RPCResponseMessage) msg;
            String sequenceId = responseMessage.getSequenceId();
            Response response = requests.get(sequenceId);
            response.setResponseMessage(responseMessage);
            LockSupport.unpark(response.getThread());
        }
        super.channelRead(ctx, msg);
    }
    
    public Object request(RPCRequestMessage requestMessage) {
        String sequenceId = requestMessage.getSequenceId();
        Response response = new Response();
        response.setThread(Thread.currentThread());
        requests.put(sequenceId, response);
        context.writeAndFlush(requestMessage);
        // 即使提前 unpark 即在 park 前 Server 进行了响应，park 方法也会不挂起当前线程。
        // LockSupport 的 park 方法可以理解为只有 {0, 1} 两个值的信号量（即多次 unpark 只会使得 permit 最大为 1）
        LockSupport.park();
        Response resp = requests.get(sequenceId);
        RPCResponseMessage responseMessage = resp.getResponseMessage();
        Exception exceptionValue = responseMessage.getExceptionValue();
        if (exceptionValue != null) {
            // Do something
        }
        Object returnValue = responseMessage.getReturnValue();
        return returnValue;
    }
}