package org.cqq.exampleprovider.service.impl;

import org.cqq.exampleinterface.interfaces.TestService;
import org.springframework.stereotype.Component;

@Component
public class TestServiceImpl implements TestService {

    @Override
    public String test(String msg) {
        return String.format("Hello %s", msg);
    }
}