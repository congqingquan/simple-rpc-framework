package org.cqq.rpc.framework.netty.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:42:59
 *
 * @Description 消息体
 */
@Data
public abstract class Message implements Serializable {

    private Integer type;

    private String sequenceId;

    public abstract Integer supportType();

    public static class Type {

        public static final int PING_MESSAGE = 0;

        public static final int PONG_MESSAGE = 1;

        public static final int RPC_REQUEST_MESSAGE = 2;

        public static final int RPC_RESPONSE_MESSAGE = 3;
    }

    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(Type.PING_MESSAGE, PingMessage.class);
        messageClasses.put(Type.PONG_MESSAGE, PongMessage.class);
        messageClasses.put(Type.RPC_REQUEST_MESSAGE, RPCRequestMessage.class);
        messageClasses.put(Type.RPC_RESPONSE_MESSAGE, RPCResponseMessage.class);
    }

    public static Class<? extends Message> getMessageClass(int type) {
        return messageClasses.get(type);
    }
}
