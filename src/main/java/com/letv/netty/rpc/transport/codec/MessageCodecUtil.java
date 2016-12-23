package com.letv.netty.rpc.transport.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/20
 * Time: 10:52
 */
public interface MessageCodecUtil {
    int MESSAGE_LENGTH = 4;

    void encode(final ByteBuf out, final Object message) throws IOException;

    Object decode(byte[] body) throws IOException;
}
