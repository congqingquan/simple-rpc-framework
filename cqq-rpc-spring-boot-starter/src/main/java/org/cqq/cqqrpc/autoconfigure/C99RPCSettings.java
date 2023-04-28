package org.cqq.cqqrpc.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by QQ.Cong on 2023-04-28 / 13:58
 *
 * @Description RPC Server configuration
 */
@ConfigurationProperties("cqq-rpc")
public class C99RPCSettings {

    private String serverName;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}