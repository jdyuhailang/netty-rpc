package com.letv.netty.rpc.serialization;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/23 15:04
 */
public class LedoDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(LedoDecoder.class);

    public LedoDecoder(int maxFrameLength){
        /*
        int maxFrameLength,     最大值
        int lengthFieldOffset,  魔术位2B，然后是长度4B，所以偏移：2
        int lengthFieldLength,  总长度占4B，所以长度是：4
        int lengthAdjustment,   总长度的值包括自己，剩下的长度=总长度-4B 所以调整值是：-4
        int initialBytesToStrip 前面6位不用再读取了，可以跳过，所以跳过的值是：6
        */
        super(maxFrameLength, 2, 4, -4, 6);
    }

    @Override
    public Object decodeFrame(ByteBuf frame) {
        Object result = ProtocolUtil.decode(frame);
        if(logger.isTraceEnabled()) {
            logger.trace("decoder result:{}", result);
        }
        return result;
    }
}
