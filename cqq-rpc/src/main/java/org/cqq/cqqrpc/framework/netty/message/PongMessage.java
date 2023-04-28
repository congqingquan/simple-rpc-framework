package org.cqq.cqqrpc.framework.netty.message;

public class PongMessage extends Message {

    @Override
    public Integer supportType() {
        return Type.PONG_MESSAGE;
    }
}
