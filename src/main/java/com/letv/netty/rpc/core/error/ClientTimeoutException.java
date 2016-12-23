package com.letv.netty.rpc.core.error;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/23 11:27
 */
public class ClientTimeoutException extends RpcException {

    /**
     * Instantiates a new Client timeout exception.
     */
    protected ClientTimeoutException() {
    }

    /**
     * Instantiates a new Client timeout exception.
     *
     * @param errorMsg
     *         the error msg
     */
    public ClientTimeoutException(String errorMsg) {
        super(errorMsg);
    }

    public ClientTimeoutException(int code, String errorMsg) {
        super(code, errorMsg);
    }

    /**
     * Instantiates a new Client timeout exception.
     *
     * @param code
     *         the error code
     *
     * @param errorMsg
     *         the error msg
     * @param throwable
     *         the throwable
     */
    public ClientTimeoutException(int code,String errorMsg, Throwable throwable) {
        super(code,errorMsg, throwable);
    }
}
