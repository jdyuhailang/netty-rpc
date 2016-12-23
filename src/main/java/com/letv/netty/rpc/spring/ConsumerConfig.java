package com.letv.netty.rpc.spring;

import com.letv.netty.rpc.client.Client;
import com.letv.netty.rpc.client.ClientChannelHandler;
import com.letv.netty.rpc.client.FailoverClient;
import com.letv.netty.rpc.core.LedoContext;
import com.letv.netty.rpc.server.ClientProxyInvoker;
import com.letv.netty.rpc.server.Invoker;
import com.letv.netty.rpc.server.ProxyFactory;
import com.letv.netty.rpc.utils.ClassLoaderUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 14:09
 */
public class ConsumerConfig<T> implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerConfig.class);
    private  String id;

    private String interfaceId;
    private String proxy = Constants.PROXY_JAVASSIST;
    /**
     * 代理的Invoker对象
     */
    protected transient volatile Invoker proxyInvoker;

    public Invoker getProxyInvoker() {
        return proxyInvoker;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    protected String protocol = Constants.DEFAULT_PROTOCOL;
    /**
     * 直连调用地址
     */
    protected String url;
    /**
     * 连接超时时间
     */
    protected int connectTimeout = Constants.DEFAULT_CLIENT_CONNECT_TIMEOUT;
    /**
     * 关闭超时时间（如果还有请求，会等待请求结束或者超时）
     */
    protected int disconnectTimeout = Constants.DEFAULT_CLIENT_DISCONNECT_TIMEOUT;

    /**
     * 集群处理，默认是failover
     */
    protected String cluster = Constants.CLUSTER_FAILOVER;

    /**
     * The Retries. 失败后重试次数
     */
    protected int retries = Constants.DEFAULT_RETRIES_TIME;

    /**
     * The Loadbalance. 负载均衡
     */
    protected String loadbalance = Constants.LOADBALANCE_RANDOM;

    /**
     * 是否延迟建立长连接,
     * connect transport when invoke, but not when init
     */
    protected boolean lazy = false;

    /**
     * 粘滞连接，一个断开才选下一个
     * change transport when current is disconnected
     */
    protected boolean sticky = false;

    /**
     * 是否jvm内部调用（provider和consumer配置在同一个jvm内，则走本地jvm内部，不走远程）
     */
    protected boolean inJVM = true;

    /**
     * 是否强依赖（即没有服务节点就启动失败）
     */
    protected boolean check = false;

    /**
     * 默认序列化
     */
    protected String serialization = Constants.DEFAULT_CODEC;
    /**
     * 线程池类型
     */
    protected String threadpool = Constants.THREADPOOL_TYPE_CACHED;

    /**
     * 业务线程池大小
     */
    protected int threads = Constants.DEFAULT_CLIENT_BIZ_THREADS;

    /**
     * io线程池大小
     */
    protected int iothreads;

    /**
     * Consumer给Provider发心跳的间隔
     */
    protected int heartbeat = Constants.DEFAULT_HEARTBEAT_TIME;

    /**
     * Consumer给Provider重连的间隔
     */
    protected int reconnect = Constants.DEFAULT_RECONNECT_TIME;

    /**
     * 最大数据包大小
     */
    protected  int payload = Constants.DEFAULT_PAYLOAD;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getDisconnectTimeout() {
        return disconnectTimeout;
    }

    public void setDisconnectTimeout(int disconnectTimeout) {
        this.disconnectTimeout = disconnectTimeout;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getLoadbalance() {
        return loadbalance;
    }

    public void setLoadbalance(String loadbalance) {
        this.loadbalance = loadbalance;
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isInJVM() {
        return inJVM;
    }

    public void setInJVM(boolean inJVM) {
        this.inJVM = inJVM;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        this.threadpool = threadpool;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getIothreads() {
        return iothreads;
    }

    public void setIothreads(int iothreads) {
        this.iothreads = iothreads;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public int getReconnect() {
        return reconnect;
    }

    public void setReconnect(int reconnect) {
        this.reconnect = reconnect;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }
    /**
     * 服务别名
     */
    protected String alias = "";
    /**
     * Gets alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets alias.
     *
     * @param alias
     *         the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
    /**
     * 代理实现类
     */
    protected transient volatile T proxyIns;
    /**
     * 代理接口类，和T对应，主要针对泛化调用
     */
    protected transient volatile Class proxyClass;

    /**
     * 调用类
     */
    protected transient volatile Client client;
    public Client getClient() {
        return client;
    }

    public T getProxyIns() {
        return proxyIns;
    }

    public synchronized T refer(){

        int version = LedoContext.getVersion();
        logger.info("version {} ..." ,version);
        getProxyClass();
        logger.info(this.toString());
        if (proxyIns != null) {
            return proxyIns;
        }
        try {
            client = new FailoverClient(this);
            ClientChannelHandler clientChannelHandler = (ClientChannelHandler)client.getChannel().pipeline().get(com.letv.netty.rpc.utils.Constants.CLIENT_CHANNELHANDLE_NAME);
            clientChannelHandler.setClient(client);
            proxyInvoker = new ClientProxyInvoker(this);
            proxyIns = (T) ProxyFactory.buildProxy(getProxy(), getProxyClass(),proxyInvoker);
        }catch (Exception e) {
        }
        return  proxyIns;
    }
    protected Class<?> getProxyClass() {
        if (proxyClass != null) {
            return proxyClass;
        }
        try {
            if (StringUtils.isNotBlank(interfaceId)) {
                this.proxyClass = ClassLoaderUtils.forName(interfaceId);
                if (!proxyClass.isInterface()) {
                    logger.error("consumer.interface",
                            interfaceId, "interfaceId must set interface class, not implement class");
                }
            } else {
                logger.error("consumer.interface",
                        interfaceId, "interfaceId must set interface class, not implement class");
            }
        } catch (ClassNotFoundException t) {
            throw new IllegalStateException(t.getMessage(), t);
        }
        return proxyClass;
    }
    /**
     * Unrefer void.
     */
    public synchronized void unrefer() {
        if (proxyIns == null) {
            return;
        }
        try {
            client.destroy();
        } catch (Exception e) {
            logger.warn("Catch exception when unRefer consumer config : "
                    + ", but you can ignore if it's called by JVM shutdown hook", e);
        }
        // 清除一些缓存
        proxyIns = null;
    }

    @Override
    public String toString() {
        return "ConsumerConfig{" +
                "id='" + id + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", proxy='" + proxy + '\'' +
                ", protocol='" + protocol + '\'' +
                ", url='" + url + '\'' +
                ", alias='" + alias + '\'' +
                ", proxyClass=" + proxyClass +
                '}';
    }
}
