package com.letv.netty.rpc.serialization;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.utils.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/23 15:02
 */
public class LedoEncoder extends MessageToByteEncoder {

    private static final Logger logger = LoggerFactory.getLogger(LedoEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ByteBuf headBody = null;
        if(out == null) {
            //logger.debug("ByteBuf out is null..");
            out = ctx.alloc().buffer();
        }
        try {
            if(msg instanceof BaseMessage){
                 BaseMessage base = (BaseMessage)msg;
                 if(base.getMsg() != null){
                     write(base.getMsg(),out);
                     base.getMsg().release();
                 }else{
                     headBody = ctx.alloc().heapBuffer();
                     ProtocolUtil.encode(msg, headBody);
                     write(headBody,out);
                 }

            }else{
                //throw new LedoCodecException("Not support this type of Object.");
            }

        } finally {
            if(headBody != null)headBody.release();
        }
    }

    /**
     * 复制数据
     *
     * @param data
     *         序列化后的数据
     * @param out
     *         回传的数据
     */
    private void write(ByteBuf data, ByteBuf out) {
        int totalLength = 2 + 4 + data.readableBytes();
        if(out.capacity() < totalLength) out.capacity(totalLength);
        out.writeBytes(Constants.MAGIC_CODE_BYTE); // 写入magiccode
        int length = totalLength - 2 ; //  data.readableBytes() + 4  (4指的是FULLLENGTH)
        out.writeInt(length);   //4 for Length Field
        out.writeBytes(data, data.readerIndex(), data.readableBytes());
        //logger.trace("out length:{}",out.readableBytes());
    }
}
