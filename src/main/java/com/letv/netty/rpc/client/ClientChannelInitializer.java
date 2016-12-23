package com.letv.netty.rpc.client;

import com.letv.netty.rpc.core.error.InitErrorException;
import com.letv.netty.rpc.serialization.LedoDecoder;
import com.letv.netty.rpc.serialization.LedoEncoder;
import com.letv.netty.rpc.utils.Constants;
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
 * Time: 17:15
 */
@ChannelHandler.Sharable
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ClientChannelHandler clientChannelHandler;

    private ClientTransportConfig transportConfig;

    public ClientChannelInitializer(ClientTransportConfig transportConfig) {
        this.transportConfig = transportConfig;
        this.clientChannelHandler = new ClientChannelHandler();
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 根据服务端协议，选择解码器
        Constants.ProtocolType type = transportConfig.getProvider().getProtocolType();
        System.out.println("use protocol type : " + type);
        //ch.attr(ClientTransportConfig.PAYLOADATTR).setIfAbsent(transportConfig.getPayload());
        switch (type) {
            case ledo:

                pipeline.addLast(new LedoEncoder());
                pipeline.addLast(new LedoDecoder(transportConfig.getPayload()));
                break;
            default:
                throw new InitErrorException("Unsupported client protocol type : " + type.name());
        }
        pipeline.addLast(Constants.CLIENT_CHANNELHANDLE_NAME,clientChannelHandler);
    }

    public ClientChannelHandler getClientChannelHandler() {
        return clientChannelHandler;
    }
}
