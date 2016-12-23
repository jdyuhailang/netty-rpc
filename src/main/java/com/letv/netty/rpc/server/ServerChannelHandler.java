package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.RequestMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 13:24
 */
@ChannelHandler.Sharable
public class ServerChannelHandler  extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);
    BaseServerHandler serverHandler;
    private  ServerTransportConfig serverTransportConfig;
    public ServerChannelHandler(ServerTransportConfig serverTransportConfig){
        this.serverHandler = HandlerFactory.getServerHandler(serverTransportConfig);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("server channel is inactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (msg instanceof RequestMessage) {

            //RequestMessage requestMessage = (RequestMessage)msg;
            serverHandler.handlerRequest(channel,msg);
            //logger.info("RequestMessage {}" ,requestMessage.getClassName());
        }else{

            logger.info("Only support base message {}" , msg.getClass().getName()) ;
        }

        //logger.info("server channel is reading message");

        //
        /*if (msg instanceof RequestMessage) {
            RequestMessage requestMsg = (RequestMessage) msg;
            logger.info("server channel is reading message : {} " ,requestMsg.getAlias());
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*if (this == ctx.pipeline().last()) {
            logger.warn(
                    "EXCEPTION, please implement " + getClass().getName() +
                            ".exceptionCaught() for proper handling.", cause.getCause());
        }*/
        logger.info("server channel is exception {} " ,cause);

    }
}
