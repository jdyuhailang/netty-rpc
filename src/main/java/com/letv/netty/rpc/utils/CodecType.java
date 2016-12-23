package com.letv.netty.rpc.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:05
 */
public enum CodecType {
    @Deprecated dubbo(1),
    hessian(2),
    java(3),
    @Deprecated compactedjava(4),
    json(5),
    @Deprecated fastjson(6),
    @Deprecated nativejava(7),
    @Deprecated kryo(8),
    msgpack(10),
    @Deprecated nativemsgpack(11),
    protobuf(12);

    private int value;

    CodecType(int mvalue) {
        this.value = mvalue;
    }

    public int value() {
        return value;
    }

    public static CodecType valueOf(int value) {
        CodecType p;
        switch (value) {
            case 10:
                p = msgpack;
                break;
            case 2:
                p = hessian;
                break;
            case 3:
                p = java;
                break;
            case 12:
                p = protobuf;
                break;
            case 5:
                p = json;
                break;
            case 1:
                p = dubbo;
                break;
            case 11:
                p = nativemsgpack;
                break;
            case 4:
                p = compactedjava;
                break;
            case 6:
                p = fastjson;
                break;
            case 7:
                p = nativejava;
                break;
            case 8:
                p = kryo;
                break;
            default:
                throw new IllegalArgumentException("Unknown codec type value: " + value);
        }
        return p;
    }
}
