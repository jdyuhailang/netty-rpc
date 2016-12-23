package com.letv.netty.rpc.server;

import com.letv.netty.rpc.core.error.RpcException;
import com.letv.netty.rpc.spring.ProviderConfig;
import com.letv.netty.rpc.transport.NamedThreadFactory;
import com.letv.netty.rpc.utils.PooledBufHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 11:49
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private ServerBootstrap serverBootstrap;
    private ServerTransportConfig serverTransportConfig;

    private InvokerHandler handler;

    private  static final int threads = Math.max(4, Runtime.getRuntime().availableProcessors() / 2);
    public Server(ServerTransportConfig serverTransportConfig) {
        this.serverTransportConfig = serverTransportConfig;
        this.handler = HandlerFactory.getServerHandler(this.serverTransportConfig);
    }

    public void registerProcessor(ProviderConfig providerConfig, Invoker instance) {
        String insKey = genInstanceName(providerConfig.getInterfaceId(), providerConfig.getAlias());
        logger.info("insKey {} " ,insKey);
        handler.registerProcessor(insKey,instance);
    }

    public static String genInstanceName(String interfaceId,String alias){
        if( interfaceId == null || interfaceId.trim().length() <= 0){
            throw new RpcException("interfaceId cannot be null!");
        }
        if( alias == null || alias.trim().length() <=0){
            return interfaceId;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(interfaceId);
        builder.append("/");
        builder.append(alias);
        return builder.toString();
    }

    public void start() {
        boolean flag = Boolean.FALSE;
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(this.serverTransportConfig.getParentEventLoopGroup(), this.serverTransportConfig.getChildEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer(serverTransportConfig));
        serverBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverTransportConfig.getCONNECTTIMEOUT())
                .option(ChannelOption.SO_BACKLOG, serverTransportConfig.getBACKLOG())
                .option(ChannelOption.SO_REUSEADDR, true)   //disable this on windows, open it on linux
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledBufHolder.getInstance())
                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .childOption(ChannelOption.SO_RCVBUF,8192 * 128)
                .childOption(ChannelOption.SO_SNDBUF,8192 * 128)
                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(serverTransportConfig.getHost(), serverTransportConfig.getPort()));
        ChannelFuture channelFuture = future.addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("Server have success bind to {}:{}", serverTransportConfig.getHost(), serverTransportConfig.getPort());
                } else {
                    logger.error("Server fail bind to {}:{}", serverTransportConfig.getHost(), serverTransportConfig.getPort());
                    serverTransportConfig.getChildEventLoopGroup().shutdownGracefully();
                    serverTransportConfig.getParentEventLoopGroup().shutdownGracefully();
                }
            }
        });

        try {
            channelFuture.await(5000, TimeUnit.MILLISECONDS);
            if(channelFuture.isSuccess()){
                flag = Boolean.TRUE;
                logger.info("Server have success  {}",flag);
            }

        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }
    }
    public boolean isStarted() {
        return serverTransportConfig != null;
    }
    public void stop() {
        logger.info("Shutdown the Ledo server transport now...");
        serverTransportConfig.getChildEventLoopGroup().shutdownGracefully();
        serverTransportConfig.getParentEventLoopGroup().shutdownGracefully();
    }
}
