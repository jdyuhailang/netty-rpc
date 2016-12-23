package com.letv.netty.rpc.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 13:01
 */
public class RpcServerSpringStarter {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerSpringStarter.class);
    public static void main(String[] args) {
        logger.info("start...");
        new ClassPathXmlApplicationContext("applicationProvider.xml");
        synchronized (RpcServerSpringStarter.class) {
            while (true) {
                try {
                    RpcServerSpringStarter.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
