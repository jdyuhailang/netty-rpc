package com.letv.netty.rpc.server;

import io.netty.channel.Channel;


/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 14:24
 */
public interface InvokerHandler {
    /**
     * 注册服务，将Invoker注册到端口
     *
     * @param instanceName
     *         服务实例关键字
     * @param invoker
     */
    void registerProcessor(String instanceName, Invoker invoker);

    /**
     * 反注册服务，从端口上删掉Invoker
     *
     * @param instanceName
     *         服务实例关键字
     */
    void unregisterProcessor(String instanceName);

    /**
     * 处理请求（可以实时或者丢到线程池）
     *
     * @param channel
     *         连接（结果可以写入channel）
     * @param requestMsg
     *         请求
     */
    void handlerRequest(Channel channel, Object requestMsg);

    /**
     * 关闭服务
     */
    void shutdown();
}
