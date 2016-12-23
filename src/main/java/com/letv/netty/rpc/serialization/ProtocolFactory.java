package com.letv.netty.rpc.serialization;


import com.letv.netty.rpc.core.error.InitErrorException;
import com.letv.netty.rpc.utils.CodecType;
import com.letv.netty.rpc.utils.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/22 17:35
 */
public class ProtocolFactory {

    /**
     * The constant protocolMap.
     */
    private static Map<Integer, Protocol> protocolMap = new ConcurrentHashMap<Integer, Protocol>();


    static {
        Protocol protocol1 = initProtocol(Constants.ProtocolType.ledo, CodecType.msgpack);
        Protocol protocol2 = initProtocol(Constants.ProtocolType.dubbo, CodecType.hessian);
        protocolMap.put(buildKey(Constants.ProtocolType.ledo.value(), CodecType.msgpack.value()), protocol1);
        protocolMap.put(buildKey(Constants.ProtocolType.dubbo.value(), CodecType.hessian.value()), protocol2);
    }

    /**
     * Gets protocol.
     *
     * @param protocolType
     *         the protocol type
     * @param codecType
     *         the codec type
     * @return the protocol
     */
    public static Protocol getProtocol(int protocolType, int codecType) {
        int key = buildKey(protocolType, codecType);
        Protocol ins;
        ins = protocolMap.get(key);
        if (ins == null) {
            ins = initProtocol(Constants.ProtocolType.valueOf(protocolType), CodecType.valueOf(codecType));
            protocolMap.put(key, ins);
        }
        return ins;
    }

    /**
     * Gets protocol.
     *
     * @param protocolType
     *         the protocol type
     * @param codecType
     *         the codec type
     * @return protocol
     * @see
     */
    public static Protocol getProtocol(Constants.ProtocolType protocolType, CodecType codecType) {
        int key = buildKey(protocolType.value(), codecType.value());
        Protocol ins;
        ins = protocolMap.get(key);
        if (ins == null) {
            ins = initProtocol(protocolType, codecType);
            protocolMap.put(key, ins);
        }
        return ins;
    }

    /**
     * Build key.
     *
     * @param protocolType
     *         the protocol type
     * @param codecType
     *         the codec type
     * @return the int
     */
    private static int buildKey(int protocolType, int codecType) {
        return protocolType << 8 + codecType;
    }

    /**
     * Init protocol.
     *
     * @param protocolType
     *         the protocol type
     * @param codecType
     *         the codec type
     * @return the protocol
     */
    private static Protocol initProtocol(Constants.ProtocolType protocolType, CodecType codecType) {
        Protocol ins = null;
        switch (protocolType) {
            case ledo:
                ins = new LedoProtocol(codecType);
                break;
            case dubbo:
                ins = new DubboProtocol(codecType);
                break;
            default:
                throw new InitErrorException("Init protocol error by protocolType:" + protocolType
                        + " and codecType:" + codecType);
        }
        return ins;
    }

    /**
     * 检查协议和序列化是否匹配
     *
     * @param protocolType
     *         协议
     * @param codecType
     *         序列化方式
     * @throws InitErrorException 不匹配
     */
    public static void check(Constants.ProtocolType protocolType, CodecType codecType) {
        switch (protocolType) {
            case ledo:
                if (codecType == CodecType.msgpack
                        || codecType == CodecType.hessian
                        || codecType == CodecType.java
                        || codecType == CodecType.json
                        || codecType == CodecType.protobuf) {
                    break;
                } else {
                    throw new InitErrorException(1012,"Serialization of protocol ledo only support" +
                            " \"msgpack\", \"hessian\", \"java\", \"json\"");
                }
            case dubbo:
                if (codecType == CodecType.hessian
                        || codecType == CodecType.java) {
                    break;
                } else {
                    throw new InitErrorException(1013,"Serialization of protocol dubbo only support" +
                            " \"hessian\" and \"java\"!");
                }
            case rest: // 不使用序列化
                break;
            case webservice: // 不使用序列化
                break;
            case jaxws: // 不使用序列化
                break;
            default:
                throw new InitErrorException(1014,"Unsupported protocol group by protocol type:" + protocolType
                        + " and codec type:" + codecType);
        }
    }
}
