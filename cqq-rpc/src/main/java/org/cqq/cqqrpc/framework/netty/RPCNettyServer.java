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
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.cqq.cqqrpc.framework.common.constants.DubboRemotingConstants;
import org.cqq.cqqrpc.framework.common.constants.GlobalConfig;
import org.cqq.cqqrpc.framework.netty.handler.RPCNettyServerHandler;
import org.cqq.cqqrpc.framework.netty.handler.protocol.ProtocolFrameDecoder;
import org.cqq.cqqrpc.framework.util.NetUtils;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by QQ.Cong on 2023-04-26 / 13:50
 *
 * @Description RPC netty server
 */
@Slf4j
public class RPCNettyServer {

    private static RPCNettyServer instance;

    private final String serverName;

    private Channel serverChannel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public RPCNettyServer(String serverName) {
        this.serverName = serverName;
    }

    public static RPCNettyServer getInstance() {
        return instance;
    }

    public void start() {
        log.info("{} starting...", serverName);
        // group
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, String.format("%s-%s", serverName, DubboRemotingConstants.EVENT_LOOP_SERVER_BOSS_POOL_NAME));
        workerGroup = NettyEventLoopFactory.eventLoopGroup(
                DubboRemotingConstants.DEFAULT_IO_THREADS, String.format("%s-%s", serverName, DubboRemotingConstants.EVENT_LOOP_SERVER_WORKER_POOL_NAME));
        // handler
        // LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
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
                        // ch.pipeline().addLast("logging-handler", loggingHandler);
                        // ch.pipeline().addLast("server-idle-handler",
                        //         new IdleStateHandler(0, 0, DubboRemotingConstants.DEFAULT_IDLE_TIMEOUT_MILLISECONDS, MILLISECONDS));
                        ch.pipeline().addLast("rpc-netty-server-handler", rpcNettyServerHandler);
                    }
                });
        // bind
        ChannelFuture channelFuture = bootstrap.bind("localhost", 9010).syncUninterruptibly();
        serverChannel = channelFuture.channel();
        // wait
        new Thread(() -> serverChannel.closeFuture().syncUninterruptibly(), serverName).start();
        // registry shutdown hook
        registryShutdownHook();
        // set instance
        RPCNettyServer.instance = this;
        log.info("{} started", serverName);
    }

    public void shutdown() {
        log.info("{} shutdown...", serverName);
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
        log.info("{} shutdown", serverName);
    }

    private void registryShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, String.format("%s-cleaner", serverName)));
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