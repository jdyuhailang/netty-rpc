package com.letv.netty.rpc.spring;

import com.letv.netty.rpc.core.LedoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:14
 */
public class ConsumerBean<T> extends ConsumerConfig<T>  implements InitializingBean,
        FactoryBean,
        ApplicationContextAware,
        DisposableBean,
        BeanNameAware {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerBean.class);

    private ApplicationContext applicationContext;

    private transient String beanName;

    private transient T object;

    private transient Class objectType;
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public Object getObject() throws Exception {
        logger.info("Generate consumer object");
        //object = refer();
        object = LedoContext.isUnitTestMode() ? null : refer();
        return object;
    }

    public Class<?> getObjectType() {
        try {
            //如果spring注入在前，reference操作在后，则会提前走到此方法，此时interface为空
            objectType = super.getProxyClass();
        } catch (Exception e) {
            objectType = null;
        }
        return objectType;
    }
    public void destroy() throws Exception {
        logger.info("LEDO destroy consumer with bean name : {}", beanName);
        super.unrefer();
    }
    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
