package com.letv.netty.rpc.server;

import com.letv.netty.rpc.spring.Constants;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:47
 */
public final class ProxyFactory {
    /**
     * 构建代理类实例
     *
     * @param proxy
     *         代理类型
     * @param clazz
     *         原始类
     * @param proxyInvoker
     *         代码执行的Invoker
     * @param <T>
     *         类型
     * @return 代理类实例
     * @throws Exception
     */
    public static <T> T buildProxy(String proxy, Class<T> clazz, Invoker proxyInvoker) throws Exception {
        if (Constants.PROXY_JAVASSIST.equals(proxy)) {
            return JavassistProxy.getProxy(clazz,proxyInvoker);
        } else if (Constants.PROXY_JDK.equals(proxy)) {
            return JDKProxy.getProxy(clazz,proxyInvoker);
        } else {
            //throw new IllegalConfigureException(1045, "consumer.proxy", proxy);
            return null;
        }
    }
}
