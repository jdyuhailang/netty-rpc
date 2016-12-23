package com.letv.netty.rpc.client;

import com.letv.netty.rpc.transport.NamedThreadFactory;
import com.letv.netty.rpc.utils.Constants;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 17:08
 */
public class ClientTransportConfig {


    private final static Logger logger = LoggerFactory.getLogger(ClientTransportConfig.class);

    private Provider provider; // 对应的Provider信息

    private int connectionTimeout = Constants.DEFAULT_CLIENT_CONNECT_TIMEOUT;// 默认连接超时时间

    private int invokeTimeout = Constants.DEFAULT_CLIENT_INVOKE_TIMEOUT; // 默认的调用超时时间（长连接调用时会被覆盖）

    private int clientBusinessPoolSize = Constants.DEFAULT_CLIENT_BIZ_THREADS; //默认线程池

    private String clientBusinessPoolType = Constants.THREADPOOL_TYPE_CACHED; // 默认线程池类型

    private int childNioEventThreads = 0; // worker线程==IO线程

    private boolean useEpoll = false; // 默认false

    private int payload = Constants.DEFAULT_PAYLOAD; // 最大数据量

    private static EventLoopGroup eventLoopGroup; // 全局共用

    public static final AttributeKey<Integer> PAYLOADATTR = AttributeKey.valueOf(Constants.PAYLOAD);

    /*
     *default timeout 2000 ms
     */
    public ClientTransportConfig(Provider provider) {
        this.provider = provider;
    }

    public ClientTransportConfig(Provider provider, int connectionTimeout){
        this.provider = provider;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 注意：如果复用连接，此处存的是第一个建立的provider的信息，接口名和alias可能不正确。
     * @return Provider
     */
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean isUseEpoll() {
        return useEpoll;//default enable Epoll on linux platform
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public int getClientBusinessPoolSize() {
        return clientBusinessPoolSize;
    }

    public void setClientBusinessPoolSize(int clientBusinessPoolSize) {
        this.clientBusinessPoolSize = clientBusinessPoolSize;
    }

    public String getClientBusinessPoolType() {
        return clientBusinessPoolType;
    }

    public void setClientBusinessPoolType(String clientBusinessPoolType) {
        this.clientBusinessPoolType = clientBusinessPoolType;
    }

    public int getChildNioEventThreads() {
        return childNioEventThreads;
    }

    public void setChildNioEventThreads(int childNioEventThreads) {
        this.childNioEventThreads = childNioEventThreads;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public int getPayload() {
        return payload;
    }

    public int getInvokeTimeout() {
        return invokeTimeout;
    }

    public void setInvokeTimeout(int invokeTimeout) {
        this.invokeTimeout = invokeTimeout;
    }

    public static EventLoopGroup getEventLoopGroup(ClientTransportConfig transportConfig){
        if (eventLoopGroup == null || eventLoopGroup.isShutdown()) {
            initEventLoop(transportConfig);
        }
        return eventLoopGroup;
    }

    private static synchronized void initEventLoop(ClientTransportConfig transportConfig) {
        if (eventLoopGroup == null || eventLoopGroup.isShutdown()) {
            int childNioEventThreads = transportConfig.getChildNioEventThreads();
            int threads = childNioEventThreads > 0 ?
                    childNioEventThreads : // 用户配置
                    Math.max(6, Constants.DEFAULT_IO_THREADS); // 默认cpu+1,至少6个
            NamedThreadFactory threadName = new NamedThreadFactory("LEDO-CLI-WORKER", true);
            if (transportConfig.isUseEpoll()) {
                eventLoopGroup = new EpollEventLoopGroup(threads, threadName);
            } else {
                eventLoopGroup = new NioEventLoopGroup(threads, threadName);
            }
        }
    }

    public static void closeEventGroup(){
        logger.debug("close Client EventLoopGroup...");
        if (eventLoopGroup != null && !eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
        }
        eventLoopGroup = null;
    }
}