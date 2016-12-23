package com.letv.netty.rpc.server;

import com.letv.netty.rpc.core.error.InitErrorException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 14:42
 */
public class HandlerFactory {
    public static BaseServerHandler getServerHandler(ServerTransportConfig config) {
        BaseServerHandler handler = null;
        switch (config.getProtocolType()) {
            case ledo:
                handler = BaseServerHandler.getInstance(config);
                break;
            default:
                throw new InitErrorException(1021,"Unsupported protocol type of server handler:" + config.getProtocolType());
        }
        return handler;
    }
}
