package com.letv.netty.rpc.server;

import com.letv.netty.rpc.serialization.LedoDecoder;
import com.letv.netty.rpc.serialization.LedoEncoder;
import com.letv.netty.rpc.spring.ServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 13:16
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ServerChannelHandler serverChannelHandler;
    /**
     * 最大数据包大小 maxFrameLength
     */
    private int payload = 8 * 1024 * 1024;
    private ServerTransportConfig serverTransportConfig;
    public ServerChannelInitializer(ServerTransportConfig serverTransportConfig) {
        this.serverTransportConfig = serverTransportConfig;
        serverChannelHandler = new ServerChannelHandler(serverTransportConfig);
    }
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LedoDecoder(payload));
        pipeline.addLast(new LedoEncoder());
        pipeline.addLast(serverChannelHandler);
    }
}
