package com.letv.netty.rpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 16:42
 */
public class ProviderBean<T> extends ProviderConfig<T> implements
        InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener,
        BeanNameAware {
    private static final Logger logger = LoggerFactory.getLogger(ProviderBean.class);

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void destroy() throws Exception {

    }

    public void afterPropertiesSet() throws Exception {

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        doExport();
    }
}
