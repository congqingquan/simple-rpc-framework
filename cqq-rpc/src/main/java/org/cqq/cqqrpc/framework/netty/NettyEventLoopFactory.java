package org.cqq.cqqrpc.framework.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.cqq.cqqrpc.framework.common.constants.DubboCommonConstants;
import org.cqq.cqqrpc.framework.common.constants.DubboRemotingConstants;

import java.util.concurrent.ThreadFactory;

/**
 * Created by QQ.Cong on 2023-04-26 / 14:22:20
 *
 * @Description Netty event loop factory
 */
public class NettyEventLoopFactory {

    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ? new EpollEventLoopGroup(threads, threadFactory) : new NioEventLoopGroup(threads, threadFactory);
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    private static boolean shouldEpoll() {
        if (Boolean.parseBoolean(System.getProperty(DubboRemotingConstants.NETTY_EPOLL_ENABLE_KEY))) {
            String osName = System.getProperty(DubboCommonConstants.OS_NAME_KEY);
            return osName.toLowerCase().contains(DubboCommonConstants.OS_LINUX_PREFIX) && Epoll.isAvailable();
        }
        return false;
    }
}
