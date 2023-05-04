package org.cqq.cqqrpc.framework.netty.handler.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.cqq.cqqrpc.framework.common.constants.GlobalConfig;
import org.cqq.cqqrpc.framework.netty.message.Message;
import org.cqq.cqqrpc.framework.netty.serialize.Serializer;
import org.cqq.cqqrpc.framework.util.EnumUtils;
import org.cqq.cqqrpc.framework.util.SequenceIdGenerator;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:42:42
 *
 * @Description JSON 消息解码器 (拆分为两个类MessageToByteEncoder  & !MessageToByteEncoder)
 */
@Slf4j
@ChannelHandler.Sharable
public class JsonMessageCodec extends MessageToMessageCodec<ByteBuf, Message> {
    
    /**
     * 自定义协议:
     * <p>
     * 魔数，用来在第一时间判定是否是无效数据包
     * 版本号，可以支持协议的升级
     * 序列化算法，消息正文到底采用哪种序列化反序列化方式，可以由此扩展，例如: json、protobuf、hessian、jdk
     * 指令类型，是登录、注册、单聊、群聊... 跟业务相关
     * 请求序号，为了双工通信，提供异步能力
     * 正文长度
     * 消息正文
     */
    
    public static Integer getContentLengthFieldOffset() {
        return 3 + 4 + 1 + 1 + 32;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(GlobalConfig.MAGIC_NUM.getBytes()); // 3
        out.writeFloat(GlobalConfig.VERSION); // 4
        out.writeByte(GlobalConfig.DEFAULT_SERIALIZE_ALGORITHM.getType()); // 1
        out.writeByte(msg.supportType()); // 1
        out.writeBytes(SequenceIdGenerator.getSequenceId().getBytes(Charset.defaultCharset())); // 32
        byte[] content = GlobalConfig.DEFAULT_SERIALIZE_ALGORITHM.serialize(msg);
        out.writeInt(content.length); // 4
        out.writeBytes(content);
        outList.add(out);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String magicNum = msg.readBytes(GlobalConfig.MAGIC_NUM.length()).toString(Charset.defaultCharset());
        float version = msg.readFloat();
        byte serializeType = msg.readByte();
        byte messageType = msg.readByte();
        String sequenceId = msg.readBytes(32).toString(Charset.defaultCharset());
        int contentLength = msg.readInt();
        byte[] tempBuf = new byte[contentLength];
        msg.readBytes(tempBuf, 0, contentLength);
        
        Serializer.Algorithm algorithm =
                EnumUtils.equalMatch(Serializer.Algorithm.values(), Serializer.Algorithm::getType, Byte.valueOf(serializeType).intValue())
                        .orElseThrow(() -> new RuntimeException("Invalid serialize type"));
        Object message = algorithm.deserialize(Message.getMessageClass(messageType), tempBuf);

//        log.info("Magic num [{}] Version [{}] Serialize type [{}] MessageType [{}] Sequence id [{}] Content length [{}]",
//                magicNum, version, serializeType, messageType, sequenceId, contentLength);
//        log.info("Sharable message: {}", message);
        
        out.add(message);
    }
}