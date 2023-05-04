package org.cqq.cqqrpc.framework.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.cqq.cqqrpc.framework.common.constants.DubboRemotingConstants;
import org.cqq.cqqrpc.framework.common.constants.GlobalConfig;
import org.cqq.cqqrpc.framework.netty.handler.RPCNettyClientHandler;
import org.cqq.cqqrpc.framework.netty.handler.protocol.ProtocolFrameDecoder;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;

/**
 * Created by QQ.Cong on 2023-04-28 / 14:56
 *
 * @Description RPC netty client
 */
public class RPCNettyClient {

    private static final RPCNettyClient client = new RPCNettyClient();

    public static RPCNettyClient getInstance() {
        return client;
    }

    private final Channel clientChannel;

    private final RPCNettyClientHandler clientHandler;

    public RPCNettyClient() {
        EventLoopGroup group = NettyEventLoopFactory.eventLoopGroup(1, DubboRemotingConstants.EVENT_LOOP_CLIENT_BOSS_POOL_NAME);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NettyEventLoopFactory.socketChannelClass());
        bootstrap.group(group);

        // handler
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        ChannelDuplexHandler messageCodec = GlobalConfig.DEFAULT_SHARABLE_MESSAGE_CODEC;
        clientHandler = new RPCNettyClientHandler();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("protocol-frame-decode-handler", new ProtocolFrameDecoder());
                ch.pipeline().addLast("message-codec-handler", messageCodec);
//                ch.pipeline().addLast("logging-handler", loggingHandler);
                ch.pipeline().addLast("rpc-netty-client-handler", clientHandler);
            }
        });
        clientChannel = bootstrap.connect("localhost", 9010).channel();
    }

    public Object request(RPCRequestMessage requestMessage) {
        return clientHandler.request(requestMessage);
    }
}