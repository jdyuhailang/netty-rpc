package com.letv.netty.rpc.spring;

import com.letv.netty.rpc.server.ProviderProxyInvoker;
import com.letv.netty.rpc.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 16:44
 */
public class ProviderConfig<T> {
    private final static Logger logger = LoggerFactory.getLogger(ProviderConfig.class);

    /*---------- 参数配置项开始 ------------*/
    /**
     * 接口实现类引用
     */
    protected transient T ref;

    /**
     * 配置的协议列表
     */
    protected List<ServerConfig> server;

    /**
     * 服务发布延迟,单位毫秒，默认0，配置为-1代表spring加载完毕（通过spring才生效）
     */
    protected int delay = Constants.DEFAULT_PROVIDER_DELAY;

    /**
     * 权重
     */
    protected int weight = Constants.DEFAULT_PROVIDER_WEIGHT;

    /**
     * 包含的方法
     */
    protected String include = "*";

    /**
     * 不发布的方法列表，逗号分隔
     */
    protected String exclude;

    /**
     * 是否动态注册，默认为true，配置为false代表不主动发布，需要到管理端进行上线操作
     */
    protected boolean dynamic = true;

    /**
     * 服务优先级，越大越高
     */
    protected int priority = Constants.DEFAULT_METHOD_PRIORITY;

    /**
     * whitelist blacklist
     */

    /*-------- 下面是方法级配置 --------*/

    /**
     * 接口下每方法的最大可并行执行请求数，配置-1关闭并发过滤器，等于0表示开启过滤但是不限制
     */
    protected int concurrents = 0;

    /*---------- 参数配置项结束 ------------*/

    /**
     * 是否已发布
     */
    protected transient volatile boolean exported;

    /**
     * 方法名称：是否可调用
     */
    protected transient volatile ConcurrentHashMap<String, Boolean> methodsLimit;
    private String alias;

    protected String interfaceId;

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    /**
     * 发布的服务配置
     */
    /*private final static ConcurrentHashSet<String> EXPORTED_KEYS
            = new ConcurrentHashSet<String>();*/
    /**
     * Gets server.
     *
     * @return the server
     */
    public List<ServerConfig> getServer() {
        return server;
    }

    /**
     * Sets server.
     *
     * @param server the server
     */
    public void setServer(List<ServerConfig> server) {
        this.server = server;
    }

    /**
     * Gets ref.
     *
     * @return the ref
     */
    public T getRef() {
        return ref;
    }

    /**
     * Sets ref.
     *
     * @param ref the ref
     */
    public void setRef(T ref) {
        this.ref = ref;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets weight.
     *
     * @param weight the weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Gets include.
     *
     * @return the include
     */
    public String getInclude() {
        return include;
    }

    /**
     * Sets include.
     *
     * @param include the include
     */
    public void setInclude(String include) {
        this.include = include;
    }

    /**
     * Gets exclude.
     *
     * @return the exclude
     */
    public String getExclude() {
        return exclude;
    }

    /**
     * Sets exclude.
     *
     * @param exclude the exclude
     */
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    /**
     * Gets delay.
     *
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets delay.
     *
     * @param delay the delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Is dynamic.
     *
     * @return the boolean
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Sets dynamic.
     *
     * @param dynamic the dynamic
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Gets priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets priority.
     *
     * @param priority the priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets concurrents.
     *
     * @return the concurrents
     */
    public int getConcurrents() {
        return concurrents;
    }

    /**
     * Sets concurrents.
     *
     * @param concurrents
     *         the concurrents
     */
    public void setConcurrents(int concurrents) {
        this.concurrents = concurrents;
    }

    /**
     * 是否有并发控制需求，有就打开过滤器
     * 配置-1关闭并发过滤器，等于0表示开启过滤但是不限制
     *
     * @return 是否配置了concurrents boolean
     */
    public boolean hasConcurrents() {
        return concurrents >= 0;
    }

    /**
     * add server.
     *
     * @param server ServerConfig
     */
    public void setServer(ServerConfig server) {
        if (this.server == null) {
            this.server = new ArrayList<ServerConfig>();
        }
        this.server.add(server);
    }
    /**
     * 发布服务
     */
    public synchronized void doExport()  {
        logger.info(exported + "" + alias);
        logger.info("ref {}" ,ref.getClass().getName());
        List<ServerConfig> serverConfigs = getServer();
        ProviderProxyInvoker invoker = new ProviderProxyInvoker(this);
        if (serverConfigs != null) {
            for (ServerConfig serverConfig : serverConfigs) {
                logger.info(serverConfig.getPort() + "");
                serverConfig.start();
                Server server = serverConfig.getServer();

                server.registerProcessor(this,invoker);
            }
        }
        logger.info("export provider service...");
        exported = true;
    }
}
