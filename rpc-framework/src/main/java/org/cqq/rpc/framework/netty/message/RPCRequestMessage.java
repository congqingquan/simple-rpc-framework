package org.cqq.rpc.framework.netty.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:43:08
 *
 * @Description RPC 请求消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RPCRequestMessage extends Message {

    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private String interfaceName;
    /**
     * 调用接口中的方法名
     */
    private String methodName;
    /**
     * 方法返回类型
     */
    private Class<?> returnType;
    /**
     * 方法参数类型数组
     */
    private Class<Object>[] parameterTypes;
    /**
     * 方法参数值数组
     */
    private Object[] parameterValue;

    public RPCRequestMessage(String sequenceId, String interfaceName, String methodName,
                             Class<?> returnType, Class<Object>[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public Integer supportType() {
        return Type.RPC_REQUEST_MESSAGE;
    }
}