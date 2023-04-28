package org.cqq.exampleinterface.interfaces;

import org.cqq.cqqrpc.framework.common.annotation.Remoting;

@Remoting
public interface TestService {

    String test(String msg);
}