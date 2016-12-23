package com.letv.netty.rpc.core.error;

import java.util.Collection;

/**
 * Title: 远程调用时，没有可用的服务端 <br>
 * <p/>
 * Description: <br>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class NoAliveProviderException extends RpcException {


    /**
     * Instantiates a new No alive provider exception.
     *
     * @param key
     *         the key
     * @param serverIp
     *         the server ip
     */
    public NoAliveProviderException(String key, String serverIp) {
        super(10000,"No alive provider of pinpoint address : [" + serverIp + "]! The key is " + key);
    }

    /**
     * Instantiates a new No alive provider exception.
     *
     * @param key
     *         the key
     * @param providers
     *         the providers
     */
    public NoAliveProviderException(String key, Collection providers) {
        super(10001,"No alive provider! The key is " + key + ", current providers is " + providers);
    }

    /**
     * Instantiates a new No alive provider exception.
     */
    protected NoAliveProviderException() {

    }
}
