package org.cqq.exampleconsumer;

import org.cqq.exampleinterface.interfaces.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @RequestMapping("/test")
    public String test(String msg) {
        return testService.test(msg);
    }
}