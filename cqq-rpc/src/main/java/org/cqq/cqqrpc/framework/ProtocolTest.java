package org.cqq.cqqrpc.framework;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.cqq.cqqrpc.framework.netty.handler.protocol.ProtocolFrameDecoder;
import org.cqq.cqqrpc.framework.netty.handler.protocol.JsonMessageCodec;
import org.cqq.cqqrpc.framework.netty.message.RPCRequestMessage;

import java.net.InetSocketAddress;

/**
 * Created by QQ.Cong on 2023-04-12 / 14:44
 *
 * @Description 自定义协议
 */
@Slf4j
public class ProtocolTest {

    static JsonMessageCodec jsonMessageCodec = new JsonMessageCodec();

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                server();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Server thread").start();


        new Thread(() -> {
            try {
                client();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "Client thread").start();
    }

    static void server() throws Exception {
        Channel serverChannel = new ServerBootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("Server thread poll")))
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                            }

                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ProtocolFrameDecoder());
                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                ch.pipeline().addLast(jsonMessageCodec);
                            }
                        }
                )
                .bind(new InetSocketAddress("localhost", 9000))
                .sync()
                .channel();
        serverChannel.close();
    }

    static void client() throws Exception {
        Channel clientChannel = new Bootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("Client thread poll")))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(jsonMessageCodec);
                    }
                })
                .connect(new InetSocketAddress("localhost", 9000))
                // 阻塞等待连接成功
                .sync()
                // 获取连接后的通道
                .channel();
        // 写入数据
        RPCRequestMessage rpcRequestMessage = new RPCRequestMessage();
        rpcRequestMessage.setInterfaceName("java.lang.String");
        clientChannel.writeAndFlush(rpcRequestMessage);

        System.in.read();
    }
}