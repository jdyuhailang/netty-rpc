package com.letv.netty.rpc.serialization;

import com.letv.netty.rpc.core.LedoContext;
import com.letv.netty.rpc.core.error.RpcException;
import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.MessageHeader;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.message.ResponseMessage;
import com.letv.netty.rpc.utils.CompressUtil;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.ExceptionUtils;
import com.letv.netty.rpc.utils.PooledBufHolder;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/23
 * Time: 11:57
 */
public class ProtocolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolUtil.class);



    public static ByteBuf encode(Object object, ByteBuf byteBuf ){
        int protocolType;
        int codeType;
        MessageHeader header = null;
        try {
            if(object instanceof BaseMessage){
                BaseMessage msg = (BaseMessage)object;
                protocolType = msg.getProtocolType();
                codeType = msg.getMsgHeader().getCodecType();
            }else{
                throw new RpcException("encode object shout be a instance of BaseMessage.");
            }
            Protocol protocol = ProtocolFactory.getProtocol(protocolType, codeType);
            byteBuf =protocol.encode(object,byteBuf);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            RpcException rException = ExceptionUtils.handlerException(header,e);
            throw rException;
        }
        return byteBuf;
    }



    public static BaseMessage decode(ByteBuf byteBuf){
        MessageHeader header = null;
        Integer msgLength = byteBuf.readableBytes() + 6;//magiccode + msg length(4 byte)
        BaseMessage msg = null;
        ByteBuf deCompress = null;
        try {
            Short headerLength = byteBuf.readShort();
//            int readerIndex = byteBuf.readerIndex();
//            if(readerIndex > byteBuf.readableBytes()){
//                throw new LedoCodecException("codecError:header length error.");
//            }
            //ByteBuf headerBuf = byteBuf.slice(readerIndex,headerLength);
            //byteBuf.skipBytes(headerLength);
            header = decodeHeader(byteBuf, headerLength);
            header.setHeaderLength(headerLength);
            //是否需要解压
            int compType = header.getCompressType();
            if (compType > 0) {
                if(logger.isTraceEnabled()) {
                    logger.trace("msgId [{}] is deCompressed with processType {}",
                            header.getMsgId(), Constants.CompressType.valueOf((byte) compType));
                }

                byte[] desc = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(desc);
                byte[] deCom = CompressUtil.deCompress(desc, (byte)compType);
                deCompress = PooledBufHolder.getBuffer(deCom.length);
                deCompress.writeBytes(deCom);
                byteBuf.release();

                header.setLength(deCompress.readableBytes() + 6 + headerLength);
                msg = enclosure(deCompress,header);
            } else {
                header.setLength(msgLength);
                msg = enclosure(byteBuf,header);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            RpcException rpcException = ExceptionUtils.handlerException(header, e);
            byteBuf.release();//release the byteBuf when decode hit on error
            if (deCompress != null) deCompress.release();
            throw rpcException;
        }
        return msg;
    }

    public static BaseMessage enclosure(ByteBuf byteBuf, MessageHeader header){
        int msgType = header.getMsgType();
        BaseMessage msg = null;
        try {
            switch(msgType){
//                case Constants.CALLBACK_REQUEST_MSG:
//                    Protocol protocol = ProtocolFactory.getProtocol(header.getProtocolType(), header.getCodecType());
//                    RequestMessage tmpCallbackMsg = new RequestMessage();
//                    Invocation tmp1 = (Invocation)protocol.decode(byteBuf,Invocation.class.getCanonicalName());
//                    tmpCallbackMsg.setInvocationBody(tmp1);
//                    byteBuf.release();
//                    msg = tmpCallbackMsg;
//                    break;
                case Constants.CALLBACK_REQUEST_MSG:
                case Constants.REQUEST_MSG:
                    RequestMessage tmp = new RequestMessage();
                    tmp.setReceiveTime(LedoContext.systemClock.now());
                    tmp.setMsgBody(byteBuf.slice(byteBuf.readerIndex(),byteBuf.readableBytes()));
                    msg = tmp;
                    break;
                case Constants.CALLBACK_RESPONSE_MSG:
                    Protocol protocol1 = ProtocolFactory.getProtocol(header.getProtocolType(), header.getCodecType());
                    ResponseMessage response1 =(ResponseMessage)protocol1.decode(byteBuf,ResponseMessage.class.getCanonicalName());
                    msg = response1;
                    byteBuf.release();
                    break;
                case Constants.RESPONSE_MSG:
                    ResponseMessage response = new ResponseMessage();
                    //byteBuf.retain();
                    response.setMsgBody(byteBuf.slice(byteBuf.readerIndex(),byteBuf.readableBytes()));
                    //Protocol protocol = ProtocolFactory.getProtocol(header.getProtocolType(), header.getCodecType());
                    //ResponseMessage response =(ResponseMessage)protocol.decode(byteBuf,ResponseMessage.class.getCanonicalName()); deserialized in the userThread
                    msg = response;
                    break;
                case Constants.HEARTBEAT_REQUEST_MSG:
                    msg = new RequestMessage();
                    byteBuf.release();
                    break;
                case Constants.HEARTBEAT_RESPONSE_MSG:
                    msg = new ResponseMessage();
                    byteBuf.release();
                    break;
                case Constants.SHAKEHAND_MSG:
                    RequestMessage shakeHand = new RequestMessage();
                    msg = shakeHand;
                    byteBuf.release();
                    break;
                default:
                    throw new RpcException(header,"Unknown message type in header!");
            }
            if (msg != null) {
                msg.setMsgHeader(header);
            }
        } catch (Exception e) {
            RpcException rException = ExceptionUtils.handlerException(header,e);
            throw rException;
        }
        return msg;
    }

    /**
     * Decode报文头
     *
     * @param byteBuf
     *         报文
     * @param headerLength
     *         报文长度
     * @return MessageHeader
     */
    private static MessageHeader decodeHeader(ByteBuf byteBuf, int headerLength){
        byte protocolType = byteBuf.readByte();
        byte codecType = byteBuf.readByte();
        byte msgType = byteBuf.readByte();
        byte compressType = byteBuf.readByte();
        int messageId = byteBuf.readInt();
        MessageHeader header = new MessageHeader();
        header.setValues(protocolType,codecType,msgType,compressType,messageId);
        if (headerLength > 8) {
            bytes2Map(header.getAttrMap(), byteBuf);
        }
        return header;
    }

    private static void bytes2Map(Map<Byte, Object> dataMap, ByteBuf byteBuf){
        byte size = byteBuf.readByte();
        for (int i = 0; i < size; i++) {
            byte key = byteBuf.readByte();
            byte type = byteBuf.readByte();
            if (type == 1) {
                int value = byteBuf.readInt();
                dataMap.put(key, value);
            } else if (type == 2) {
                int length = byteBuf.readShort();
                byte[] dataArr = new byte[length];
                byteBuf.readBytes(dataArr);
                dataMap.put(key, new String(dataArr));
            } else if (type == 3) {
                byte value = byteBuf.readByte();
                dataMap.put(key, value);
            } else if (type == 4) {
                short value = byteBuf.readShort();
                dataMap.put(key, value);
            } else {
                throw new RpcException("Value of attrs in message header must be byte/short/int/string");
            }
        }
    }
}
