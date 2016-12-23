package com.letv.netty.rpc.serialization;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 11:24
 */
public interface Decoder {

    /**
     * 反序列化，按指定类
     *
     * @param datas
     *         byte[]数据
     * @param clazz
     *         指定class类
     * @return 实际对象
     */
    Object decode(byte[] datas, Class clazz);

    /**
     * 反序列化，按指定类名
     *
     * @param datas
     *         byte[]数据
     * @param className
     *         指定class类名
     * @return 实际对象
     */
    Object decode(byte[] datas, String className);
}

