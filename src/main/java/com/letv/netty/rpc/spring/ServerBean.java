package com.letv.netty.rpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 15:56
 */
public class ServerBean<T> extends ServerConfig implements
        InitializingBean, DisposableBean, BeanNameAware {

    private static final Logger logger = LoggerFactory.getLogger(ServerBean.class);

    private transient String beanName;

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void destroy() throws Exception {
        logger.info("Stop LEDO server with beanName {}", beanName);
        stop();
    }

    public void afterPropertiesSet() throws Exception {

    }
}