package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.RequestMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 10:54
 */
public class BaseServerHandler implements InvokerHandler{
    private final static Logger logger = LoggerFactory.getLogger(BaseServerHandler.class);
    /**
     * 业务线程池（一个端口一个）
     */
    private final ExecutorService bizThreadPool; // 业务线程池
    /**
     * 当前handler的Invoker列表 一个接口+alias对应一个Invoker
     */
    private Map<String, Invoker> instanceMap = new ConcurrentHashMap<String, Invoker>();/**
     * 一个端口对应一个ServerHandler
     */
    private static Map<String,BaseServerHandler> serverHandlerMap = new ConcurrentHashMap<String, BaseServerHandler>();

    public Map<String, Invoker> getInstanceMap() {
        return instanceMap;
    }

    public void setInstanceMap(Map<String, Invoker> instanceMap) {
        this.instanceMap = instanceMap;
    }

    private final ServerTransportConfig serverTransportConfig;
    public BaseServerHandler(ServerTransportConfig transportConfig){
        this.serverTransportConfig = transportConfig;
        this.bizThreadPool = Executors.newCachedThreadPool();
    }
    public void handlerRequest(Channel channel, Object requestMsg) {
        RequestMessage msg = (RequestMessage)requestMsg;
        BaseTask task = new BaseTask(this,channel,msg);
        bizThreadPool.submit(task);
    }

    public static BaseServerHandler getInstance(ServerTransportConfig transportConfig) {
        BaseServerHandler baseHandler = null;
        baseHandler = serverHandlerMap.get(transportConfig.getServerTransportKey());
        if (baseHandler == null) {
            baseHandler = new BaseServerHandler(transportConfig);
            serverHandlerMap.put(transportConfig.getServerTransportKey(), baseHandler);
        }
        return baseHandler;
    }

    public void registerProcessor(String instanceName, Invoker invoker) {
        instanceMap.put(instanceName, invoker);
    }

    public void unregisterProcessor(String instanceName) {
        if (instanceMap.containsKey(instanceName)) {
            instanceMap.remove(instanceName);
        } else {
            throw new RuntimeException("[Ledo-16002]No such invoker key when unregister processor:" + instanceName);
        }
    }

    public void shutdown() {
        if(!bizThreadPool.isShutdown()) {
            logger.debug("ServerHandler's business thread pool shutdown..");
            bizThreadPool.shutdown();
        }
    }
}
