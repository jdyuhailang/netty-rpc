package com.letv.netty.rpc.serialization;

import com.letv.netty.rpc.serialization.hessian.HessianCodec;
import com.letv.netty.rpc.utils.CodecType;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 11:25
 */
public class CodecFactory {
    /**
     * 得到Codec实现
     *
     * @param codecType
     *         codec type
     * @return Codec实现
     */
    public static Codec getInstance(int codecType) {
        CodecType ct = CodecType.valueOf(codecType);
        return getInstance(ct);
    }

    /**
     * 得到Codec实现
     *
     * @param codecType
     *         CodecType枚举
     * @return Codec实现
     */
    public static Codec getInstance(CodecType codecType) {
        Codec ins = null;
        switch (codecType) {
            /*case msgpack:
                ins = new MsgpackCodec();
                break;*/
            case hessian:
                ins = new HessianCodec();
                break;
           /* case java:
                ins = new JavaCodec();
                break;
            case json:
                ins = new JsonCodec();
                break;*/
            default:
                break;
        }

        return ins;
    }
}
