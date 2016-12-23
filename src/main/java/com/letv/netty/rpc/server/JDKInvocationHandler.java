package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:14
 */
public class JDKInvocationHandler implements InvocationHandler {

    private Invoker proxyInvoker;

    public JDKInvocationHandler(Invoker proxyInvoker) {
        this.proxyInvoker = proxyInvoker;
    }

    /**
     * @see InvocationHandler#invoke(Object,
     * Method, Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] paramValues)
            throws Throwable {
        String methodName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        if ("toString".equals(methodName) && paramTypes.length == 0) {
            return proxyInvoker.toString();
        } else if ("hashCode".equals(methodName) && paramTypes.length == 0) {
            return proxyInvoker.hashCode();
        } else if ("equals".equals(methodName) && paramTypes.length == 1) {
            return proxyInvoker.equals(paramValues[0]);
        }
        RequestMessage requestMessage = BaseMessage.buildRequest(method.getDeclaringClass(),
                methodName, paramTypes, paramValues);
        ResponseMessage responseMessage = proxyInvoker.invoke(requestMessage);
        if(responseMessage.isError()){
            throw responseMessage.getException();
        }
        return responseMessage.getResponse();
    }
}
