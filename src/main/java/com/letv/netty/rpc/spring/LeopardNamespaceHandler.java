package com.letv.netty.rpc.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 15:55
 */
public class LeopardNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        registerBeanDefinitionParser("server", new LeopardBeanDefinitionParser(ServerBean.class, true));
        registerBeanDefinitionParser("provider", new LeopardBeanDefinitionParser(ProviderBean.class, true));
        registerBeanDefinitionParser("consumer", new LeopardBeanDefinitionParser(ConsumerBean.class, true));
    }
}
