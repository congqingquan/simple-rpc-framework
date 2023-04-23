package org.cqq.rpc.framework.netty.message;

public class PingMessage extends Message {

    @Override
    public Integer supportType() {
        return Type.PING_MESSAGE;
    }
}
