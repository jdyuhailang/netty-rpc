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
public class RpcServerHessianProtocolStarter {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerHessianProtocolStarter.class);
    public static void main(String[] args) {
        logger.info("start...");
        new ClassPathXmlApplicationContext("rpc-invoke-config-hessian.xml");
    }
}
