package com.letv.netty.rpc.test;

import com.letv.netty.rpc.core.LedoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:23
 */
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    public static void main(String[] args) throws  Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationConsumer.xml" });
        Calculate service = (Calculate) context.getBean("demoService");
        int result = service.add(1,1);
        logger.info("result {} ",result);
        //TimeUnit.MILLISECONDS.sleep(100);
        /*result = service.add(1,1);
        logger.info("result {} ",result);
        result = service.add(1,1);
        logger.info("result {} ",result);*/
    }
}
