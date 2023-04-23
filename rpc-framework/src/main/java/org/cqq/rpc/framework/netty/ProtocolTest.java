package org.cqq.rpc.framework.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.cqq.rpc.framework.netty.message.RPCRequestMessage;
import org.cqq.rpc.framework.netty.protocol.ProtocolFrameDecoder;
import org.cqq.rpc.framework.netty.protocol.codec.JsonMessageCodec;

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
        server();
        client();
    }

    static void server() {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1))
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ProtocolFrameDecoder());
                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                                ch.pipeline().addLast(jsonMessageCodec);
                            }
                        }
                )
                .bind(new InetSocketAddress("localhost", 8080));
    }

    static void client() throws InterruptedException {
        RPCRequestMessage rpcRequestMessage = new RPCRequestMessage();
        rpcRequestMessage.setInterfaceName("java.lang.String");
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(jsonMessageCodec);
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                // 阻塞等待连接成功
                .sync()
                // 获取连接后的通道
                .channel()
                // 写入数据
                .writeAndFlush(rpcRequestMessage);
    }
}