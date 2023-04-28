package org.cqq.cqqrpc.framework.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;
import org.cqq.cqqrpc.framework.netty.message.RPCResponseMessage;

/**
 * Created by QQ.Cong on 2023-04-28 / 15:54
 *
 * @Description PRC netty client handler
 */
@ChannelHandler.Sharable
public class RPCNettyClientHandler extends SimpleChannelInboundHandler<RPCResponseMessage> {

    private ChannelHandlerContext context;

    private Object currentResponse;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, RPCResponseMessage response) throws Exception {
        currentResponse = response.getReturnValue();
        notify();
    }

    public synchronized Object request(RPCRequestMessage requestMessage) throws InterruptedException {
        context.writeAndFlush(requestMessage);
        wait();
        return currentResponse;
    }
}