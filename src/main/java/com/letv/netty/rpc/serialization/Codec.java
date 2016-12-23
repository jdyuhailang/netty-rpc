package com.letv.netty.rpc.serialization;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/22
 * Time: 11:24
 */
public interface Codec extends Encoder,Decoder{

    /**
     * 空的Object数组，无参方法
     */
    Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * 空的Class数组，无参方法
     */
    Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];


    /**
     * The constant RESPONSE_WITH_EXCEPTION.
     */
    byte RESPONSE_WITH_EXCEPTION = 0;
    /**
     * The constant RESPONSE_VALUE.
     */
    byte RESPONSE_VALUE = 1;
    /**
     * The constant RESPONSE_NULL_VALUE.
     */
    byte RESPONSE_NULL_VALUE = 2;
}