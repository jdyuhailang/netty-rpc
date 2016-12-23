package com.letv.netty.rpc.server;

import com.letv.netty.rpc.spring.ServerConfig;
import com.letv.netty.rpc.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 11:59
 */
public class ServerFactory {
    private static Logger logger = LoggerFactory.getLogger(ServerFactory.class);
    public static Server getServer(ServerConfig serverConfig) {
        logger.info(serverConfig.toString());
        Server server = initServer(serverConfig);
        return server;
    }

    private static Server initServer(ServerConfig serverConfig) {
        ServerTransportConfig serverTransportConfig = convertConfig(serverConfig);
        return  new Server(serverTransportConfig);
    }

    private static ServerTransportConfig convertConfig(ServerConfig serverConfig) {
        ServerTransportConfig serverTransportConfig = new ServerTransportConfig();
        serverTransportConfig.setPort(serverConfig.getPort());
        serverTransportConfig.setProtocolType(Constants.ProtocolType.valueOf(serverConfig.getProtocol()));
        serverTransportConfig.setHost(serverConfig.getHost());
        serverTransportConfig.setPrintMessage(serverConfig.isDebug());
        serverTransportConfig.setContextPath(serverConfig.getContextpath());
        serverTransportConfig.setServerBusinessPoolSize(serverConfig.getThreads());
        serverTransportConfig.setServerBusinessPoolType(serverConfig.getThreadpool());
        serverTransportConfig.setChildNioEventThreads(serverConfig.getIothreads());
        serverTransportConfig.setMaxConnection(serverConfig.getAccepts());
        serverTransportConfig.setBuffer(serverConfig.getBuffer());
        serverTransportConfig.setPayload(serverConfig.getPayload());
        serverTransportConfig.setTelnet(serverConfig.isTelnet());
        serverTransportConfig.setUseEpoll(serverConfig.isEpoll());
        serverTransportConfig.setPoolQueueType(serverConfig.getQueuetype());
        serverTransportConfig.setPoolQueueSize(serverConfig.getQueues());
        serverTransportConfig.setDispatcher(serverConfig.getDispatcher());
        serverTransportConfig.setDaemon(serverConfig.isDaemon());
        serverTransportConfig.setParameters(serverConfig.getParameters());
        return serverTransportConfig;
    }
}
