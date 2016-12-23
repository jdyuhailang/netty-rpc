package com.letv.netty.rpc.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 17:14
 */
public class PooledBufHolder {
    private static ByteBufAllocator pooled = new UnpooledByteBufAllocator(false);

    public static ByteBufAllocator getInstance(){

        return pooled;
    }

    public static ByteBuf getBuffer(){

        return pooled.buffer();
    }

    public static ByteBuf getBuffer(int size){
        return pooled.buffer(size);
    }
}
