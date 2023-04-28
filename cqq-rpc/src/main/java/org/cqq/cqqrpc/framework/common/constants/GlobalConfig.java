package org.cqq.cqqrpc.framework.common.constants;

import io.netty.channel.ChannelDuplexHandler;
import org.cqq.cqqrpc.framework.netty.handler.protocol.JsonMessageCodec;
import org.cqq.cqqrpc.framework.netty.serialize.Serializer;

/**
 * Created by QQ.Cong on 2023-04-23 / 16:11:31
 *
 * @Description 全局配置
 */
public interface GlobalConfig {

    String MAGIC_NUM = "CQQ";

    float VERSION = 1.0F;

    Serializer.Algorithm DEFAULT_SERIALIZE_ALGORITHM = Serializer.Algorithm.JSON;

    ChannelDuplexHandler DEFAULT_SHARABLE_MESSAGE_CODEC = new JsonMessageCodec();

    Integer CONTENT_LENGTH_FIELD_OFFSET = JsonMessageCodec.getContentLengthFieldOffset();
}