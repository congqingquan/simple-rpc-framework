package org.cqq.cqqrpc.autoconfigure;

import org.cqq.cqqrpc.framework.netty.RPCNettyServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by QQ.Cong on 2023-05-04 / 13:23
 *
 * @Description Application context refreshed event listener
 */
public class C99RPCApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private final C99RPCSettings settings;

    public C99RPCApplicationStartListener(C99RPCSettings settings) {
        this.settings = settings;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        RPCNettyServer server = new RPCNettyServer(settings.getServerName());
        server.start();
    }
}