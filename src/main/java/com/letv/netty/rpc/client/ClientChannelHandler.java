package com.letv.netty.rpc.client;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.MessageHeader;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.NetUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 17:16
 */
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    public ClientChannelHandler(){

    }

    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive");
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("Channel inactive: {}", channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("从服务器接收消息");
        Channel channel = ctx.channel();
        try {
            if(msg instanceof ResponseMessage){
                ResponseMessage responseMessage = (ResponseMessage) msg;
                if(responseMessage.getMsgHeader().getMsgType() == Constants.SHAKEHAND_RESULT_MSG){
                    //TODO:set HandShake status here
                     //this.clientTransport.setShakeHandStatus();
                }
                //clientTransport.receiveResponse(responseMessage);

            } else if (msg instanceof RequestMessage) {
                RequestMessage request = (RequestMessage) msg;
                // handle heartbeat Request (dubbo)
                if(request.isHeartBeat()) {
                    ResponseMessage response = BaseMessage.buildHeartbeatResponse(request);
                    channel.writeAndFlush(response);
                }
                // handle the callback Request
                else if (request.getMsgHeader().getMsgType() == Constants.CALLBACK_REQUEST_MSG) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("handler callback request...");
                    }
                } else {

                }

            } else if (msg instanceof BaseMessage) {
                BaseMessage base = (BaseMessage) msg;
                if (logger.isTraceEnabled()) {
                    logger.error("msg id:{},msg type:{}", base.getMsgHeader().getMsgId(), base.getMsgHeader().getMsgType());
                }
            } else {
                logger.error("not a type of CustomMsg ...:{} ",msg);
                //ctx.
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            BaseMessage base = (BaseMessage)msg;
            MessageHeader header = base != null? base.getMsgHeader():null;
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("event triggered:{}",evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        logger.info("exceptionCaught");
    }
}
