package org.cqq.cqqrpc.framework.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * Created by QQ.Cong on 2023-04-26 / 17:16:04
 *
 * @Description Net utils
 */
@Slf4j
public final class NetUtils {

    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }
}