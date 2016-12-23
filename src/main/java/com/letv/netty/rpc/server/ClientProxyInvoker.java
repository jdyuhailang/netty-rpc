package com.letv.netty.rpc.server;

import com.letv.netty.rpc.client.Client;
import com.letv.netty.rpc.core.error.ClientTimeoutException;
import com.letv.netty.rpc.core.error.RpcException;
import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.MsgFuture;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.spring.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:18
 */
public class ClientProxyInvoker implements Invoker {
    /**
     * slf4j Logger for this class
     */
    private final static Logger logger = LoggerFactory.getLogger(ClientProxyInvoker.class);
    /**
     * 对应的客户端信息
     */
    private final ConsumerConfig consumerConfig;

    private Client client;

    public ClientProxyInvoker(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
        this.client = consumerConfig.getClient();
    }

    public ResponseMessage invoke(BaseMessage request) {
        logger.info("invoke consumer message :{}" ,request);
        RequestMessage requestMessage = (RequestMessage) request;
        String methodName = requestMessage.getMethodName();

        requestMessage.setAlias(consumerConfig.getAlias());
        requestMessage.setClassName(consumerConfig.getInterfaceId());
        logger.info(requestMessage.getClassName());
        MsgFuture<ResponseMessage> future = this.client.sendMsg(requestMessage);
        ResponseMessage responseMessage = BaseMessage.buildHeartbeatResponse(requestMessage);
        try{
            responseMessage = future.get(5000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e) {
        } catch (ClientTimeoutException e) {
        }

        return responseMessage;
    }
}
