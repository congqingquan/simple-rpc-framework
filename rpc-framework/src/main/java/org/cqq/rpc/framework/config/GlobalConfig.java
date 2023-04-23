package org.cqq.rpc.framework.config;

import io.netty.channel.ChannelDuplexHandler;
import org.cqq.rpc.framework.netty.protocol.codec.JsonMessageCodec;
import org.cqq.rpc.framework.netty.serialize.Serializer;

/**
 * Created by QQ.Cong on 2023-04-23 / 16:11:31
 *
 * @Description 全局配置
 */
public class GlobalConfig {

    public static final String MAGIC_NUM = "CQQ";

    public static final float VERSION = 1.0F;

    public static final Serializer.Algorithm DEFAULT_SERIALIZE_ALGORITHM = Serializer.Algorithm.JSON;

    public static final ChannelDuplexHandler DEFAULT_SHARABLE_MESSAGE_CODEC = new JsonMessageCodec();

    public static final Integer CONTENT_LENGTH_FIELD_OFFSET = JsonMessageCodec.getContentLengthFieldOffset();
}