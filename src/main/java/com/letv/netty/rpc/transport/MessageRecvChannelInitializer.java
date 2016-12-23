package com.letv.netty.rpc.transport;

import com.letv.netty.rpc.core.RpcRecvSerializeFrame;
import com.letv.netty.rpc.protocal.RpcSerializeProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 10:42
 */
public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecvChannelInitializer.class);
    private RpcSerializeProtocol protocol;
    private RpcRecvSerializeFrame frame = null;

    MessageRecvChannelInitializer buildRpcSerializeProtocol(RpcSerializeProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
        logger.info("message receive construct init...");
        System.out.println("message receive construct init...");
        frame = new RpcRecvSerializeFrame(handlerMap);
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        logger.info("message receive channel init...");
        System.out.println("message receive channel init...");
        ChannelPipeline pipeline = socketChannel.pipeline();
        frame.select(protocol, pipeline);
    }
}
