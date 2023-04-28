package org.cqq.cqqrpc.framework.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.cqq.cqqrpc.framework.netty.RPCNettyServer;
import org.cqq.cqqrpc.framework.container.spring.BeanFactory;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;
import org.cqq.cqqrpc.framework.netty.message.RPCResponseMessage;
import org.cqq.cqqrpc.framework.util.NetUtils;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by QQ.Cong on 2023-04-26 / 14:52
 *
 * @Description PRC netty server handler
 */
@Slf4j
@ChannelHandler.Sharable
public class RPCNettyServerHandler extends ChannelDuplexHandler {

    private final RPCNettyServer server;

    private final Map<String, Channel> channels;

    public RPCNettyServerHandler(RPCNettyServer server) {
        this.server = server;
        this.channels = server.getChannels();
    }

    // 处理新连接事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = getChannelKey(channel);
        log.info("新的连接请求 [{}]", remoteAddress);
        channels.put(remoteAddress, ctx.channel());
    }

    // 处理断开事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = getChannelKey(channel);
        log.info("连接已正常断开 [{}]", remoteAddress);
        channels.remove(remoteAddress, ctx.channel());
    }

    // 处理异常事件
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String remoteAddress = getChannelKey(channel);
        log.info("连接异常断开 [{}]", remoteAddress);
        channels.remove(remoteAddress, ctx.channel());
    }

    // 处理 idle timeout 空闲超时事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == event.state() || IdleState.WRITER_IDLE == event.state()) {
                Channel channel = ctx.channel();
                String remoteAddress = getChannelKey(channel);
                log.info("连接不活跃，断开连接 [{}]", remoteAddress);
                channel.close();
                channels.remove(remoteAddress, ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 处理读事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof RPCRequestMessage)) {
            return;
        }
        RPCRequestMessage request = (RPCRequestMessage) msg;
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameterValue = request.getParameterValue();
        Class<?> returnType = request.getReturnType();

        RPCResponseMessage response = new RPCResponseMessage();
        response.setSequenceId(response.getSequenceId());
        response.setType(request.getType());
        try {
            Object bean = BeanFactory.getBean(interfaceName);
            Method method = bean.getClass().getMethod(methodName, parameterTypes);
            Object methodResult = method.invoke(bean, parameterValue);
            if (method.getReturnType() == returnType) {
                throw new NoSuchMethodException(String.format("Cannot found the method why invalid return type: %s", returnType));
            }
            response.setReturnValue(methodResult);
        } catch (Exception exception) {
            response.setExceptionValue(exception);
        }
        ctx.writeAndFlush(response);
    }

    private String getChannelKey(Channel channel) {
        return NetUtils.toAddressString((InetSocketAddress) channel.remoteAddress());
    }
}