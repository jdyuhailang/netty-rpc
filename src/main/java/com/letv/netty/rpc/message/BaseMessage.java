package com.letv.netty.rpc.message;

import com.letv.netty.rpc.spring.Constants;
import com.letv.netty.rpc.utils.ClassTypeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:06
 */
public abstract class BaseMessage {

    private  MessageHeader msgHeader;

    private ByteBuf msg;

    private ByteBuf msgBody;    //just invocation body..

    private transient Channel channel;

    public ByteBuf getMsg() {
        return msg;
    }

    public void setMsg(ByteBuf msg) {
        this.msg = msg;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected BaseMessage(boolean initHeader) {
        if (initHeader) {
            msgHeader = new MessageHeader();
            // TODO 是否加版本号信息
            //msgHeader.addHeadKey(Constants.HeadKey.ledoVersion, Constants.LEDO_VERSION);
        }
    }

    public int getProtocolType(){
        return msgHeader.getProtocolType();
    }

    public MessageHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MessageHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public int getRequestId() {
        return msgHeader != null ? msgHeader.getMsgId() : -1;
    }

    /**
     * @param msgId
     */
    public void setRequestId(Integer msgId) {
        msgHeader.setMsgId(msgId);
    }

    public boolean isHeartBeat() {
        int msgType = msgHeader.getMsgType();
        return msgType == Constants.HEARTBEAT_REQUEST_MSG
                || msgType == Constants.HEARTBEAT_RESPONSE_MSG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseMessage)) return false;

        BaseMessage that = (BaseMessage) o;

        return msgHeader.equals(that.msgHeader);

    }

    /**
     * Build request.
     *
     * @param clazz
     *         the class
     * @param methodName
     *         the method name
     * @param methodParamTypes
     *         the method param types
     * @param args
     *         the args
     * @return the request message
     */
    public static RequestMessage buildRequest(Class clazz, String methodName, Class[] methodParamTypes, Object[] args) {
        Invocation invocationBody = new Invocation();
        invocationBody.setArgs(args == null ? new Object[0] : args);
        invocationBody.setArgsType(methodParamTypes);
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setInvocationBody(invocationBody);
        requestMessage.setClassName(ClassTypeUtils.getTypeStr(clazz));
        requestMessage.setMethodName(methodName);

        requestMessage.getMsgHeader().setMsgType(Constants.REQUEST_MSG);
        return requestMessage;

    }

    /**
     * Build response.
     *
     * @param request
     *         the request
     * @return the response message
     */
    public static ResponseMessage buildResponse(RequestMessage request) {
        ResponseMessage responseMessage = new ResponseMessage(false);
        responseMessage.setMsgHeader(request.getMsgHeader().clone());
        // clone后不可以再修改header的map里的值，会影响到原来对象
        responseMessage.getMsgHeader().setMsgType(Constants.RESPONSE_MSG);
        return responseMessage;
    }


    /**
     * Build response.
     *
     * @param header
     *         the MessageHeader
     * @return the response message
     */
    public static ResponseMessage buildResponse(MessageHeader header) {
        ResponseMessage responseMessage = new ResponseMessage(false);
        responseMessage.setMsgHeader(header.clone());
        // clone后不可以再修改header的map里的值，会影响到原来对象
        responseMessage.getMsgHeader().setMsgType(Constants.RESPONSE_MSG);
        return responseMessage;
    }

    /**
     * Build heartbeat request.
     *
     * @return request message
     */
    public static RequestMessage buildHeartbeatRequest() {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.getMsgHeader().setMsgType(Constants.HEARTBEAT_REQUEST_MSG);
        return requestMessage;
    }

    /**
     * Build heartbeat response.
     *
     * @param heartbeat
     *         the heartbeat
     * @return the response message
     */
    public static ResponseMessage buildHeartbeatResponse(RequestMessage heartbeat) {
        ResponseMessage responseMessage = new ResponseMessage();
        MessageHeader header = responseMessage.getMsgHeader();
        header.setMsgType(Constants.HEARTBEAT_RESPONSE_MSG);
        header.setMsgId(heartbeat.getRequestId());
        header.setProtocolType(heartbeat.getProtocolType());
        header.setCodecType(heartbeat.getMsgHeader().getCodecType());
        return responseMessage;
    }

    @Override
    public int hashCode() {
        return msgHeader.hashCode();
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
                "msgHeader=" + msgHeader +
                '}';
    }

    public ByteBuf getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(ByteBuf msgBody) {
        this.msgBody = msgBody;
    }
}