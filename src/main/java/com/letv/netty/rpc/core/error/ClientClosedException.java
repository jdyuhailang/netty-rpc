package com.letv.netty.rpc.core.error;

/**
 * Description: <br>
 *     客户端连接断开异常
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class ClientClosedException extends RpcException {


    /**
     * Instantiates a new Client closed exception.
     */
    protected ClientClosedException() {
    }

    /**
     * Instantiates a new Client closed exception.
     *
     * @param errorMsg
     *         the error msg
     */
    public ClientClosedException(String errorMsg) {
        super(errorMsg);
    }

    /**
     * Instantiates a new Client closed exception.
     *
     * @param errorMsg
     *         the error msg
     * @param throwable
     *         the throwable
     */
    public ClientClosedException(int code,String errorMsg, Throwable throwable) {
        super(code,errorMsg, throwable);
    }

}