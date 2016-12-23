package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.Invocation;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.spring.ProviderConfig;
import com.letv.netty.rpc.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 14:06
 */
public class ProviderProxyInvoker implements Invoker{
    private final static Logger logger = LoggerFactory.getLogger(ProviderProxyInvoker.class);

    /**
     * 对应的客户端信息
     */
    private final ProviderConfig providerConfig;
    public ProviderProxyInvoker(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }
    /**
     * proxy拦截的调用
     *
     * @param requestMessage
     *         请求消息
     * @return 调用结果
     */
    public ResponseMessage invoke(BaseMessage requestMessage) {
        logger.info("BaseTask invoke");
        RequestMessage request = (RequestMessage) requestMessage;
        Invocation invocation = request.getInvocationBody();
        ResponseMessage response = BaseMessage.buildResponse(request);
        try {
            // 反射 真正调用业务代码
            Object result = reflectInvoke(providerConfig.getRef(), invocation);
            response.setResponse(result);
        } catch (IllegalArgumentException e){ // 非法参数，可能是实现类和接口类不对应
            response.setException(e);
        } catch (InvocationTargetException e) { // 业务代码抛出异常
            response.setException(e.getCause());
        } catch (ClassNotFoundException e) {
            response.setException(e);
        } catch (NoSuchMethodException e) {
            response.setException(e);
        } catch (IllegalAccessException e) {
            response.setException(e);
        }
        logger.info("response : {}" ,response.toString());
        // 得到结果
        return response;
    }

    private Object reflectInvoke(Object impl, Invocation invocation) throws NoSuchMethodException,
            ClassNotFoundException, InvocationTargetException, IllegalAccessException {

        Method method = ReflectUtils.getMethod(invocation.getClazzName(),
                invocation.getMethodName(), invocation.getArgsType());
        return method.invoke(impl, invocation.getArgs());
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }
}
