package com.letv.netty.rpc.server;

import com.letv.netty.rpc.utils.ClassLoaderUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:48
 */
public class JDKProxy {

    public static <T> T getProxy(Class<T> interfaceClass, Invoker proxyInvoker) throws Exception {
        InvocationHandler handler = new JDKInvocationHandler(proxyInvoker);
        ClassLoader classLoader = ClassLoaderUtils.getCurrentClassLoader();
        T result = (T) Proxy.newProxyInstance(classLoader,
                new Class[]{interfaceClass}, handler);
        return result;
    }
}
