package org.cqq.cqqrpc.framework.util;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by QQ.Cong on 2023-04-23 / 15:44:43
 *
 * @Description 请求序列号生成器
 */
public abstract class SequenceIdGenerator {

    public static String getSequenceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(getSequenceId().getBytes(Charset.defaultCharset()).length);
    }
}
