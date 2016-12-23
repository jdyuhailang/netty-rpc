package com.letv.netty.rpc.server;

import com.letv.netty.rpc.core.LedoContext;
import com.letv.netty.rpc.core.error.RpcException;
import com.letv.netty.rpc.message.Invocation;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.serialization.*;
import com.letv.netty.rpc.utils.CodecType;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.NetUtils;
import com.letv.netty.rpc.utils.PooledBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 11:02
 */
public class BaseTask implements Runnable{
    private final static Logger logger = LoggerFactory.getLogger(BaseTask.class);
    private  Channel channel;
    private final Codec codec;
    private  RequestMessage msg;
    private final BaseServerHandler serverHandler;

    public BaseTask(BaseServerHandler serverHandler,Channel channel, RequestMessage msg) {
        this.serverHandler = serverHandler;
        this.channel = channel;
        this.msg = msg;
        this.codec = CodecFactory.getInstance(CodecType.hessian);
    }

    public void run() {
        logger.info("msg : {}" ,msg);
        Invocation invocation = null;
        Protocol protocol = ProtocolFactory.getProtocol(msg.getProtocolType(), msg.getMsgHeader().getCodecType());
        invocation = (Invocation) protocol.decode(msg.getMsgBody(), Invocation.class.getCanonicalName());
        msg.setInvocationBody(invocation);
        String className = msg.getClassName();
        String methodName = msg.getMethodName();
        String aliasName = msg.getAlias();
        logger.info("server invoke task run , className {} methodName {}",className,methodName);
        logger.info("alias {}" ,aliasName );
        long now = LedoContext.systemClock.now();
        Integer timeout = msg.getClientTimeout();
        if (timeout != null && now - msg.getReceiveTime() > timeout) { // 客户端已经超时的请求
            logger.warn("[Ledo-16009]Discard request cause by timeout after receive the msg: {}", msg.getMsgHeader());
            return;
        }

        final InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
        final InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();

        logger.info(remoteAddress.toString());
        logger.info(localAddress.toString());
        Map<String,Invoker> invokerMap = serverHandler.getInstanceMap();
        logger.info("size {}" ,invokerMap.size());
        logger.info(genInstanceName(className,aliasName));
        Invoker invoker = invokerMap.get(genInstanceName(className,aliasName));
        logger.info(invoker.getClass().getName());
        ResponseMessage responseMessage = invoker.invoke(msg);
        ByteBuf buf = PooledBufHolder.getBuffer();
        buf = ProtocolUtil.encode(responseMessage, buf);
        responseMessage.setMsg(buf);
        Future future1 = channel.writeAndFlush(responseMessage);
        future1.addListener(new FutureListener() {
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()) {
                    logger.info("Response write back {}", future.isSuccess());
                    return;
                } else if (!future.isSuccess()) {
                    Throwable throwable = future.cause();
                    logger.error("Failed to send response to "
                            + NetUtils.channelToString(localAddress, remoteAddress)
                            + " for msg id: "
                            + msg.getMsgHeader().getMsgId()
                            + ", Cause by:", throwable);
                    //throw new RpcException("Fail to send Response msg for response:" + msg.getMsgHeader(), throwable);
                }
            }
        });
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
}
