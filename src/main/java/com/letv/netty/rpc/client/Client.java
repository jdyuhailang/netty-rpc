package com.letv.netty.rpc.client;

import com.letv.netty.rpc.core.LedoContext;
import com.letv.netty.rpc.message.*;
import com.letv.netty.rpc.serialization.*;
import com.letv.netty.rpc.spring.ConsumerConfig;
import com.letv.netty.rpc.transport.NamedThreadFactory;
import com.letv.netty.rpc.utils.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 16:42
 */
public abstract  class Client {
    private final static Logger logger = LoggerFactory.getLogger(Client.class);
    /**
     * 当前服务集群对应的Consumer信息
     */
    protected final ConsumerConfig<?> consumerConfig;
    /**
     * 是否已启动(已建立连接)
     */
    protected volatile boolean inited = false;

    /**
     * 是否已经销毁（已经销毁不能再继续使用）
     */
    protected volatile boolean destroyed = false;
    private final Codec codec;
    protected Channel channel;
    public Client(ConsumerConfig<?> consumerConfig) {
        this.consumerConfig = consumerConfig;
        this.codec = CodecFactory.getInstance(CodecType.hessian);
        initConnections();
    }
    private void initConnections() {
        if (destroyed) { // 已销毁
            logger.error("[Ledo-22001]Client has been destroyed!");
        }
        if (inited) { // 已初始化
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            try {
                List<Provider> tmpProviderList = buildProviderList();
                for (Provider provider : tmpProviderList){
                    logger.info("provider list {} " ,provider);
                }
                connectToProviders(tmpProviderList);

            }catch (Exception e) {
                logger.error("Init provider's transport error!", e);
            }
            inited = true;
        }
    }
    protected void connectToProviders(List<Provider> providerList) {
        final String interfaceId = consumerConfig.getInterfaceId();
        int providerSize = providerList.size();
        logger.info("Init provider of {}, size is : {}", interfaceId, providerSize);
        if (providerSize > 0) {
            int threads = Math.min(10, providerSize); // 最大10个
            final CountDownLatch latch = new CountDownLatch(providerSize);
            ThreadPoolExecutor initPool = new ThreadPoolExecutor(threads, threads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(providerList.size()),
                    new NamedThreadFactory("LEDO-CLI-CONN-" + interfaceId, true));
            int connectTimeout = consumerConfig.getConnectTimeout();
            for (final Provider provider : providerList) {
                final ClientTransportConfig config = providerToClientConfig(provider);
                initPool.execute(new Runnable() {
                    public void run() {
                        logger.info("protocol type : {}",provider.getProtocolType());
                        Constants.ProtocolType protocolType = provider.getProtocolType();
                        try{
                            switch(protocolType){
                                case ledo:
                                    initTransport(config);
                                    break;
                                default:
                                    break;
                            }
                        }finally {
                            logger.info("latch countdown");
                            latch.countDown(); // 连上或者抛异常
                        }

                    }
                });
            }
            try {
                int totalTimeout = ((providerSize % threads == 0) ? (providerSize / threads) : ((providerSize /
                        threads) + 1)) * connectTimeout + 500;
                latch.await(totalTimeout, TimeUnit.MILLISECONDS); // 一直等到子线程都结束
            } catch (InterruptedException e) {
                logger.error("Exception when init provider", e);
            } finally {
                logger.info("initPool.shutdown()");
                initPool.shutdown(); // 关闭线程池
            }
        }
    }
    private void initTransport(ClientTransportConfig config){
        //logger.info("channel is register {}" ,this.channel.isRegistered());
        this.channel = buildChannel(config);

    }
    public static Channel buildChannel(ClientTransportConfig transportConfig){
        logger.info("transportConfig {}" ,transportConfig.getProvider());
        EventLoopGroup eventLoopGroup = ClientTransportConfig.getEventLoopGroup(transportConfig);
        //EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Channel channel = null;
        String host = transportConfig.getProvider().getIp();
        int port = transportConfig.getProvider().getPort();
        int connectTimeout = transportConfig.getConnectionTimeout();
        logger.info("" + connectTimeout);
        Class clazz = NioSocketChannel.class;
        if(transportConfig.isUseEpoll()){
            clazz = EpollSocketChannel.class;
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(clazz);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            //bootstrap.option(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
            bootstrap.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024);
            bootstrap.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
            //bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT);
            ClientChannelInitializer initializer = new ClientChannelInitializer(transportConfig);
            bootstrap.handler(initializer);
            // Bind and start to accept incoming connections.

            ChannelFuture channelFuture = bootstrap.connect(host,port);
            channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
                if (NetUtils.toAddressString((InetSocketAddress) channel.remoteAddress())
                        .equals(NetUtils.toAddressString((InetSocketAddress) channel.localAddress()))) {
                    // 服务端不存活时，连接左右两侧地址一样的情况
                    channel.close(); // 关掉重连
                    logger.error("Failed to connect " + host + ":" + port
                            + ". Cause by: Remote and local address are the same");
                }
            }else{
                Throwable cause = channelFuture.cause();
                logger.error("Failed to connect " + host + ":" + port +
                        (cause != null ? ". Cause by: " + cause.getMessage() : "."));
            }
        } catch (Exception e) {
            String errorStr = "Failed to build channel for host:" + host + " port:" + port
                    + ". Cause by: " + e.getMessage();
            logger.error(errorStr);
        }
        return channel;
    }
    private ClientTransportConfig providerToClientConfig(Provider provider) {
        ClientTransportConfig config = new ClientTransportConfig(provider, consumerConfig.getConnectTimeout());
        //config.setInvokeTimeout(consumerConfig.getTimeout());
        config.setPayload(consumerConfig.getPayload());
        config.setClientBusinessPoolType(consumerConfig.getThreadpool());
        config.setClientBusinessPoolSize(consumerConfig.getThreads());
        config.setChildNioEventThreads(consumerConfig.getIothreads());
        return config;
    }

    protected List<Provider> buildProviderList() {
        List<Provider> tmpProviderList = null;
        String url = consumerConfig.getUrl();
        if(StringUtils.isNotBlank(url)){
            tmpProviderList = new ArrayList<Provider>();
            String interfaceId = consumerConfig.getInterfaceId();
            String alias = consumerConfig.getAlias();
            Constants.ProtocolType pt = Constants.ProtocolType.valueOf(consumerConfig.getProtocol());
            String[] providerStrs = StringUtils.splitWithCommaOrSemicolon(url);
            for (int i = 0; i < providerStrs.length; i++) {
                Provider provider = Provider.valueOf(providerStrs[i]);
                tmpProviderList.add(provider);
            }
        }
        return  tmpProviderList;
    }
    public MsgFuture sendMsg(RequestMessage msg) {
        // 做一些初始化检查，例如未连接可以连接
        try {
            initConnections();
            return doSendMsg(msg);
        } finally {
        }
    }
    public MsgFuture doSendMsg(RequestMessage msg) {
        Invocation invocation = msg.getInvocationBody();
        String methodName = invocation.getMethodName();
        logger.info("channel is open {}, active {}" ,this.channel.isActive(),this.channel.isOpen());


        final MsgFuture resultFuture = new DefaultMsgFuture(this.channel, msg.getMsgHeader(), 5000);

        ByteBuf byteBuf = PooledBufHolder.getBuffer();
        Protocol protocol = ProtocolFactory.getProtocol(msg.getProtocolType(), msg.getMsgHeader().getCodecType());
        byteBuf = protocol.encode(msg, byteBuf);
        msg.setMsg(byteBuf);
        logger.info(msg.getInvocationBody().toString());
        logger.info("codec : " + msg.getMsgHeader().getCodecType());
        /*byte[] invocationData = codec.encode(msg.getInvocationBody());
        byteBuf = byteBuf.writeBytes(invocationData);*/

        this.channel.writeAndFlush(msg);
        resultFuture.setSentTime(LedoContext.systemClock.now());// 置为已发送
        return resultFuture;
    }

    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        // 关闭已有连接
        closeTransports();
        inited = false;
    }

    private void closeTransports() {
        List<Provider> tmpProviderList = buildProviderList();
        for (Provider provider : tmpProviderList){
            logger.info("provider list {} " ,provider);
        }
        int providerSize = tmpProviderList.size();
        if (providerSize > 0) {
            int threads = Math.min(10, providerSize); // 最大10个
            final CountDownLatch latch = new CountDownLatch(providerSize);
            ThreadPoolExecutor closepool = new ThreadPoolExecutor(threads, threads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(providerSize),
                    new NamedThreadFactory("LEDO-CLI-DISCONN-" + consumerConfig.getInterfaceId(), true));
            for (final Provider provider : tmpProviderList) {
                closepool.execute(new Runnable() {
                    public void run() {
                        try {
                            if(channel != null && channel.isOpen()){
                                try {
                                    channel.close();
                                } catch(Exception e) {
                                    logger.error(e.getMessage(),e);
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("catch exception but ignore it when close alive client : {}", provider);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
        }
    }
}
