package com.letv.netty.rpc.serialization;

import com.letv.netty.rpc.core.error.RpcException;
import com.letv.netty.rpc.message.*;
import com.letv.netty.rpc.utils.CodecType;
import com.letv.netty.rpc.utils.CompressUtil;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.ValueUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description: <br/>
 * <p/>
 *
 * <pre>
 *+----------------------------------------------------------------------------------------------------------------------+
 *|                                                  MAGIC CODE(1B):AEDA                                                 |
 *+----------+-----------------------------------------------------------------------------------------------------------+
 *|          |                                       FULL LENGTH:HEAD + BODY -MAGIC                                      |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                              HEAD LENGTH:Exclude Full Length and HEAD Length                              |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                  PROTOCOL TYPE(1B)                  |                    CODEC TYPE(1B)                   |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                   MESSAGE TYPE(1B)                  |                  COMPRESS TYPE(1B)                  |
 *|          +-----------------------------------------------------+-----------------------------------------------------+
 *|          |                                                 MESGID(4B)                                                |
 *|          +--------------------+--------------------------------------------------------------------------------------+
 *|HEAD      |                    |                                     MAP SIZE(1B)                                     |
 *|          |                    +--------------------------------------------------------------------------------------+
 *|          |                    |                                      MAP KEY(1B)                                     |
 *|          |                    +--------------------------------------------------------------------------------------+
 *|          |                    |                      ATTR TYPE(1B) 1:int,2:string,3:byte,4:short                     |
 *|          |    [OPT]ATTRMAP    +--------------------------------------------------------------------------------------+
 *|          |                    |                                       ATTR VAL:                                      |
 *|          |                    |                                       ?B int:4B                                      |
 *|          |                    |                            string:length(2B) +data length                            |
 *|          |                    |                                   byte:2B,short:2B                                   |
 *+----------+--------------------+--------------------------------------------------------------------------------------+
 *|          |                                             className[String]                                             |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                               alias[String]                                               |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                             methodName[String]                                            |
 *|BODY      +-----------------------------------------------------------------------------------------------------------+
 *|          |                                             argsType[String[]]                                            |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                               args[Object[]]                                              |
 *|          +-----------------------------------------------------------------------------------------------------------+
 *|          |                                              attachments[Map]                                             |
 *+----------+-----------------------------------------------------------------------------------------------------------+
 * </pre>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/22 11:28
 */
public class LedoProtocol implements Protocol {

    private static final Logger logger = LoggerFactory.getLogger(LedoProtocol.class);

    private final Codec codec;

    public LedoProtocol(CodecType codecType) {
        this.codec = CodecFactory.getInstance(codecType);
    }

    public Object decode(ByteBuf data, Class t) {
        BaseMessage msg = null;
        byte[] dataArr = new byte[data.readableBytes()];
        data.readBytes(dataArr);
        Object result = codec.decode(dataArr, t);
        return result;
    }

    public Object decode(ByteBuf datas, String classTypeName) {
        byte[] dataArr = new byte[datas.readableBytes()];
        datas.readBytes(dataArr);
        Object result = codec.decode(dataArr, classTypeName);
        return result;
    }


    public ByteBuf encode(Object obj, ByteBuf buffer) {
        logger.info("readable byte here:{}", buffer.readableBytes());
        if (obj instanceof RequestMessage) {
            RequestMessage request = (RequestMessage) obj;
            MessageHeader msgHeader = request.getMsgHeader();
            msgHeader.setCodecType(msgHeader.getCodecType());
            msgHeader.setProtocolType(msgHeader.getProtocolType());
            msgHeader.setMsgType(request.getMsgHeader().getMsgType());

            Invocation invocation = request.getInvocationBody();
            if (invocation != null) {
                byte[] invocationData = codec.encode(invocation);
                // 添加压缩判断
                byte compressType = msgHeader.getCompressType();
                if (compressType > 0 && CompressUtil.compressOpen) {
                    if (invocationData.length < CompressUtil.compressThresholdSize) { //值的大小 小于数据太小不压缩
                        msgHeader.setCompressType((byte) 0);
                    } else {
                        /*logger.debug("request msgId [{}] is deCompressed with processType {} for msgBody length {} ",
                                new Object[]{request.getMsgHeader().getMsgId(), Constants.CompressType.valueOf(
                                        (byte) request.getMsgHeader().getCompressType()), invocationData.length}
                        );*/
                        invocationData = CompressUtil.compress(invocationData, compressType);
                    }
                }
                request.getMsgHeader().setHeaderLength(encodeHeader(msgHeader, buffer)); // header
                buffer = buffer.writeBytes(invocationData); // body
                request.getMsgHeader().setLength(buffer.readableBytes());
            } else {
                encodeHeader(msgHeader, buffer);// header only
            }
        } else if (obj instanceof ResponseMessage) {
            ResponseMessage response = (ResponseMessage) obj;
            MessageHeader msgHeader = response.getMsgHeader();
            byte[] responseData = codec.encode(obj);

            // 添加压缩判断
            byte compressType = msgHeader.getCompressType();
            if (compressType > 0 && CompressUtil.compressOpen) {
                if (responseData.length < CompressUtil.compressThresholdSize) { //值的大小 小于数据太小不压缩
                    msgHeader.setCompressType((byte) 0); //CompressType.NONE.value();
                } else {
                /*logger.debug("Response msgId:[{}] is compressed  with processType {} for msgBody length {}.",
                        new Object[]{bm.getMsgHeader().getMsgId(), Constants.CompressType.valueOf(
                                (byte) bm.getMsgHeader().getCompressType()), responseData.length}
                );*/
                    responseData = CompressUtil.compress(responseData, compressType);
                }
            }
            response.getMsgHeader().setHeaderLength(encodeHeader(msgHeader, buffer)); // header
            buffer = buffer.writeBytes(responseData); // body
            response.getMsgHeader().setLength(buffer.readableBytes());

        } else {
            throw new RpcException("no such kind of  message..");

        }
        return buffer;
    }

    /**
     * encode报文头
     *
     * @param header
     *         MessageHeader
     * @param byteBuf
     *         报文
     */
    private short encodeHeader(MessageHeader header, ByteBuf byteBuf) {
        short headLength = 8; // 没有map 长度是8
        if( byteBuf.capacity() < 8 ) byteBuf.capacity(8);
        int writeIndex = byteBuf.writerIndex();
        byteBuf.writeShort(headLength);
        byteBuf.writeByte(header.getProtocolType());
        byteBuf.writeByte(header.getCodecType());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getCompressType());
        byteBuf.writeInt(header.getMsgId());
        if (header.getAttrMapSize() > 0) {
            headLength += map2bytes(header.getAttrMap(), byteBuf);
            byteBuf.setBytes(writeIndex, ValueUtil.short2bytes(headLength)); // 替换head长度的两位
        }
        return headLength;
    }


    protected short map2bytes(Map<Byte, Object> dataMap, ByteBuf byteBuf) {
        byteBuf.writeByte(dataMap.size());
        short s = 1;
        for (Map.Entry<Byte, Object> attr : dataMap.entrySet()) {
            byte key = attr.getKey();
            Object val = attr.getValue();
            if (val instanceof Integer) {
                byteBuf.writeByte(key);
                byteBuf.writeByte((byte) 1);
                byteBuf.writeInt((Integer) val);
                s += 6;
            } else if (val instanceof String) {
                byteBuf.writeByte(key);
                byteBuf.writeByte((byte) 2);
                byte[] bs = ((String) val).getBytes();
                byteBuf.writeShort(bs.length);
                byteBuf.writeBytes(bs);
                s += (4 + bs.length);
            } else if (val instanceof Byte) {
                byteBuf.writeByte(key);
                byteBuf.writeByte((byte) 3);
                byteBuf.writeByte((Byte) val);
                s += 3;
            } else if (val instanceof Short) {
                byteBuf.writeByte(key);
                byteBuf.writeByte((byte) 4);
                byteBuf.writeShort((Short) val);
                s += 4;
            } else {
                throw new RpcException("Value of attrs in message header must be byte/short/int/string");
            }
        }
        return s;
    }
}

