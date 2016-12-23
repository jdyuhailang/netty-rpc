package com.letv.netty.rpc.client;

import com.letv.netty.rpc.spring.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 16:43
 */
public class FailoverClient extends Client{
    public FailoverClient(ConsumerConfig<?> consumerConfig) {
        super(consumerConfig);
    }
}
