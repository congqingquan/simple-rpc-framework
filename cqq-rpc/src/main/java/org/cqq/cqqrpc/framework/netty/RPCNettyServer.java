package org.cqq.cqqrpc.framework.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import org.cqq.cqqrpc.framework.common.constants.DubboRemotingConstants;
import org.cqq.cqqrpc.framework.common.constants.GlobalConfig;
import org.cqq.cqqrpc.framework.netty.handler.RPCNettyServerHandler;
import org.cqq.cqqrpc.framework.netty.handler.protocol.ProtocolFrameDecoder;
import org.cqq.cqqrpc.framework.util.NetUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by QQ.Cong on 2023-04-26 / 13:50
 *
 * @Description RPC netty server
 */
public class RPCNettyServer {

    private final String serverName;

    private Channel serverChannel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public RPCNettyServer(String serverName) {
        this.serverName = serverName;
    }

    public void start() {
        // group
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, DubboRemotingConstants.EVENT_LOOP_BOSS_POOL_NAME);
        workerGroup = NettyEventLoopFactory.eventLoopGroup(DubboRemotingConstants.DEFAULT_IO_THREADS, DubboRemotingConstants.EVENT_LOOP_WORKER_POOL_NAME);
        // handler
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        ChannelDuplexHandler messageCodec = GlobalConfig.DEFAULT_SHARABLE_MESSAGE_CODEC;
        RPCNettyServerHandler rpcNettyServerHandler = new RPCNettyServerHandler(RPCNettyServer.this);
        // setting
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("protocol-frame-decode-handler", new ProtocolFrameDecoder());
                        ch.pipeline().addLast("message-codec-handler", messageCodec);
                        ch.pipeline().addLast("logging-handler", loggingHandler);
                        ch.pipeline().addLast("server-idle-handler",
                                new IdleStateHandler(0, 0, DubboRemotingConstants.DEFAULT_IDLE_TIMEOUT_MILLISECONDS, MILLISECONDS));
                        ch.pipeline().addLast("rpc-netty-server-handler", rpcNettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind("localhost", 9010).syncUninterruptibly();
        serverChannel = channelFuture.channel();
        // wait
        new Thread(() -> serverChannel.closeFuture().syncUninterruptibly(), serverName).start();
    }

    public void shutdown() {
        // unbind (cancel listen acceptable event)
        serverChannel.close();
        // shutdown boss and worker thread pool
        Future<?> bossGroupShutdownFeature = bossGroup.shutdownGracefully();
        Future<?> workGroupShutdownFeature = workerGroup.shutdownGracefully();
        bossGroupShutdownFeature.syncUninterruptibly();
        workGroupShutdownFeature.syncUninterruptibly();
        // clear client channels
        // 1. close socket channel
        channels.values().forEach(Channel::close);
        // 2. clear socket channel map
        channels.clear();
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(InetSocketAddress remoteAddress) {
        return channels.get(NetUtils.toAddressString(remoteAddress));
    }

    public int getChannelsSize() {
        return channels.size();
    }
}