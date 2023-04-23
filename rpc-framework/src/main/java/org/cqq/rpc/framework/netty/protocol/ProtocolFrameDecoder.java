package org.cqq.rpc.framework.netty.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.cqq.rpc.framework.config.GlobalConfig;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:41:56
 *
 * @Description 帧解码器
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024, GlobalConfig.CONTENT_LENGTH_FIELD_OFFSET, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}