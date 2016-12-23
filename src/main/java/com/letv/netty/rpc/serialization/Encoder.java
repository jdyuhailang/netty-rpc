package com.letv.netty.rpc.serialization;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 11:24
 */
public interface Encoder {
    /**
     * 直接序列化
     *
     * @param obj
     *         要序列化的对象
     * @return byte[]
     */
    byte[] encode(Object obj);

    /**
     * 按指定类型的序列化
     *
     * @param obj
     *         byte[]
     * @param classTypeName
     *         类型
     * @return byte[]
     */
    byte[] encode(Object obj, String classTypeName);
}
