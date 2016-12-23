package com.letv.netty.rpc.client;

import com.letv.netty.rpc.utils.CodecType;
import com.letv.netty.rpc.utils.Constants;
import com.letv.netty.rpc.utils.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 16:49
 */
public class Provider {
    /**
     * The Ip.
     */
    private String ip;

    /**
     * The Port.
     */
    private int port = 80;

    /**
     * The Protocol type.
     */
    private Constants.ProtocolType protocolType = Constants.DEFAULT_PROTOCOL_TYPE;

    /**
     * 判断服务端codec兼容性，以服务端的为准
     */
    private CodecType codecType;

    /**
     * The Weight.
     */
    private int weight = Constants.DEFAULT_PROVIDER_WEIGHT;


    /**
     * The ledo Version
     */
    private int ledoVersion;

    /**
     * The Alias.
     */
    private String alias;

    /**
     * The path
     */
    private String path;

    /**
     * The Interface id.
     */
    private String interfaceId;

    /**
     * 启用invocation优化？
     */
    private transient volatile boolean invocationOptimizing;

    /**
     * 重连周期系数：1-5（即5次才真正调一次）
     */
    private transient int reconnectPeriodCoefficient = 1;

    /**
     * Instantiates a new Provider.
     */
    public Provider() {

    }
    /**
     * Instantiates a new Provider.
     *
     * @param host the host
     * @param port the port
     */
    private Provider(String host,int port){
        this.ip = host;
        this.port = port;
    }

    /**
     * Get provider.
     *
     * @param host the host
     * @param port the port
     * @return the provider
     */
    public static Provider getProvider(String host,int port){
        return new Provider(host,port);
    }

    /**
     * Instantiates a new Provider.
     *
     * @param url the url
     */
    private Provider(final String url) {
        try {
            int protocolIndex = url.indexOf("://");
            String remainUrl;
            if (protocolIndex > -1) {
                String protocol = url.substring(0, protocolIndex).toLowerCase();
                this.setProtocolType(Constants.ProtocolType.valueOf(protocol));
                remainUrl = url.substring(protocolIndex + 3);
            } else { // 默认
                this.setProtocolType(Constants.DEFAULT_PROTOCOL_TYPE);
                remainUrl = url;
            }

            int addressIndex = remainUrl.indexOf("/");
            String address;
            if (addressIndex > -1) {
                address = remainUrl.substring(0, addressIndex);
                remainUrl = remainUrl.substring(addressIndex + 1);
            } else {
                int itfIndex = remainUrl.indexOf("?");
                if (itfIndex > -1) {
                    address = remainUrl.substring(0, itfIndex);
                    remainUrl = remainUrl.substring(itfIndex);
                } else {
                    address = remainUrl;
                    remainUrl = "";
                }
            }
            String[] ipPort = address.split(":", -1);
            this.setIp(ipPort[0]);
            this.setPort(Integer.valueOf(ipPort[1]));

            // 后面可以解析remainUrl得到interface等 /xxx?a=1&b=2
            if (remainUrl.length() > 0) {
                int itfIndex = remainUrl.indexOf("?");
                if (itfIndex > -1) {
                    String itf = remainUrl.substring(0, itfIndex);
                    this.setPath(itf);
                    // 剩下是params,例如a=1&b=2
                    remainUrl = remainUrl.substring(itfIndex + 1);
                    String[] params = remainUrl.split("&", -1);
                    for(String param: params){
                        String[] kvPair = param.split("=", -1);
                        if (Constants.CONFIG_KEY_WEIGHT.equals(kvPair[0]) && StringUtils.isNotEmpty(kvPair[1])) {
                            this.setWeight(Integer.valueOf(kvPair[1]));
                        }
                        if (Constants.CONFIG_KEY_LEDOVERSION.equals(kvPair[0]) && StringUtils.isNotEmpty(kvPair[1])) {
                            this.setLedoVersion(Integer.valueOf(kvPair[1]));
                        }
                        if (Constants.CONFIG_KEY_INTERFACE.equals(kvPair[0]) && StringUtils.isNotEmpty(kvPair[1])) {
                            this.setInterfaceId(kvPair[1]);
                        }
                        if (Constants.CONFIG_KEY_ALIAS.equals(kvPair[0]) && StringUtils.isNotEmpty(kvPair[1])) {
                            this.setAlias(kvPair[1]);
                        }
                        if (Constants.CONFIG_KEY_SERIALIZATION.equals(kvPair[0]) && StringUtils.isNotEmpty(kvPair[1])) {
                            this.setCodecType(CodecType.valueOf(kvPair[1]));
                        }
                    }
                } else {
                    String itf = remainUrl;
                    this.setPath(itf);
                }
            } else {
                this.setPath(StringUtils.EMPTY);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert url to provider, the wrong url is:" + url, e);
        }
    }

    /**
     * 从thrift://10.12.120.121:9090 得到Provider
     *
     * @param url url地址
     * @return Provider对象 provider
     */
    public static Provider valueOf(String url) {
        return new Provider(url);
    }

    /**
     * Gets protocol type.
     *
     * @return the protocol type
     */
    public Constants.ProtocolType getProtocolType() {
        return protocolType;
    }

    /**
     * Sets protocol type.
     *
     * @param protocolType the protocol type
     */
    public void setProtocolType(Constants.ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * Gets codec type.
     *
     * @return the codecType
     */
    public CodecType getCodecType() {
        return codecType;
    }

    /**
     * Sets codec type.
     *
     * @param codecType the codecType to set
     */
    public void setCodecType(CodecType codecType) {
        this.codecType = codecType;
    }



    /**
     * Gets ledo version.
     *
     * @return the ledo version
     */
    public int getLedoVersion() {
        return ledoVersion;
    }

    /**
     * Sets ledo version.
     *
     * @param ledoVersion the ledo version
     */
    public void setLedoVersion(int ledoVersion) {
        this.ledoVersion = ledoVersion;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets weight.
     *
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Gets alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets alias.
     *
     * @param alias the alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Gets interface id.
     *
     * @return the interface id
     */
    public String getInterfaceId() {
        return interfaceId;
    }

    /**
     * Sets interface id.
     *
     * @param interfaceId the interface id
     */
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 序列化到url.
     *
     * @return the string
     */
    public String toUrl(){
        String uri = protocolType + "://" + ip + ":" + port + "/" + StringUtils.trimToEmpty(path);
        StringBuilder sb = new StringBuilder();
        if (weight != Constants.DEFAULT_PROVIDER_WEIGHT) {
            sb.append("&").append(Constants.CONFIG_KEY_WEIGHT).append("=").append(weight);
        }
        if (ledoVersion > 0) {
            sb.append("&").append(Constants.CONFIG_KEY_LEDOVERSION).append("=").append(ledoVersion);
        }
        if (interfaceId != null) {
            sb.append("&").append(Constants.CONFIG_KEY_INTERFACE).append("=").append(interfaceId);
        }
        if (alias != null) {
            sb.append("&").append(Constants.CONFIG_KEY_ALIAS).append("=").append(alias);
        }
        if (codecType != null) {
            sb.append("&").append(Constants.CONFIG_KEY_SERIALIZATION).append("=").append(codecType.name());
        }
        if(sb.length() > 0){
            uri += sb.replace(0, 1, "?").toString();
        }
        return uri;
    }

    /**
     * 重写toString方法
     *
     * @return 字符串 string
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return toUrl();
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Provider)) return false;

        Provider provider = (Provider) o;

        if (port != provider.port) return false;
        if (alias != null ? !alias.equals(provider.alias) : provider.alias != null) return false;
        if (ip != null ? !ip.equals(provider.ip) : provider.ip != null) return false;
        if (interfaceId != null ? !interfaceId.equals(provider.interfaceId) : provider.interfaceId != null) return false;
        if (path != null ? !path.equals(provider.path) : provider.path != null) return false;
        if (protocolType != provider.protocolType) return false;
        return weight == provider.weight;

    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (protocolType != null ? protocolType.hashCode() : 0);
        result = 31 * result + (interfaceId != null ? interfaceId.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + weight;
        return result;
    }

    /**
     * Open invocation optimizing.
     *
     * @return the boolean
     */
    public boolean openInvocationOptimizing() {
        return invocationOptimizing;
    }

    /**
     * Sets invocation optimizing.
     *
     * @param invocationOptimizing  the invocation optimizing
     */
    public void setInvocationOptimizing(boolean invocationOptimizing) {
        this.invocationOptimizing = invocationOptimizing;
    }

    /**
     * Gets reconnect period coefficient.
     *
     * @return the reconnect period coefficient
     */
    public int getReconnectPeriodCoefficient() {
        // 最大是5
        reconnectPeriodCoefficient = Math.min(5, reconnectPeriodCoefficient);
        return reconnectPeriodCoefficient;
    }

    /**
     * Sets reconnect period coefficient.
     *
     * @param reconnectPeriodCoefficient  the reconnect period coefficient
     */
    public void setReconnectPeriodCoefficient(int reconnectPeriodCoefficient) {
        // 最小是1
        reconnectPeriodCoefficient = Math.max(1, reconnectPeriodCoefficient);
        this.reconnectPeriodCoefficient = reconnectPeriodCoefficient;
    }
}
