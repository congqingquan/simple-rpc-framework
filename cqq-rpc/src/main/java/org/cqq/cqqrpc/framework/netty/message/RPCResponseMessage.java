package org.cqq.cqqrpc.framework.netty.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:43:16
 *
 * @Description RPC 响应消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponseMessage extends Message {

    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public Integer supportType() {
        return Type.RPC_RESPONSE_MESSAGE;
    }
}
