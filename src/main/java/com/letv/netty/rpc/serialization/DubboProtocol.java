package com.letv.netty.rpc.serialization;


import com.letv.netty.rpc.core.error.InitErrorException;
import com.letv.netty.rpc.utils.ClassLoaderUtils;
import com.letv.netty.rpc.utils.CodecType;
import io.netty.buffer.ByteBuf;


/**
 * Title: dubbo协议兼容<br>
 * <p/>
 * Description: 支持hessian序列化<br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class DubboProtocol implements Protocol {

    private final Codec codec;


    public DubboProtocol() {
        //默认hessian
        this.codec = CodecFactory.getInstance(CodecType.hessian);
    }

    public DubboProtocol(CodecType codecType){
        if (codecType != CodecType.java &&
                codecType != CodecType.hessian) {
            throw new InitErrorException("Serialization of protocol dubbo only support \"hessian\" and \"java\"!");
        }
        this.codec = CodecFactory.getInstance(codecType);
    }

    public Object decode(ByteBuf datas, Class clazz) {
        byte[] databs = new byte[datas.readableBytes()];
        datas.readBytes(databs);
        return codec.decode(databs, clazz);
    }


    public Object decode(ByteBuf datas, String classTypeName) {
        try {
            Class clazz = ClassLoaderUtils.forName(classTypeName);
            return decode(datas, clazz);
        } catch (Exception e) {
            throw new InitErrorException("decode by dubbo protocol error!");
        }
    }

    public ByteBuf encode(Object obj, ByteBuf buffer) {
        byte[] bs = codec.encode(obj);
        buffer.writeBytes(bs);
        return buffer;
    }

}