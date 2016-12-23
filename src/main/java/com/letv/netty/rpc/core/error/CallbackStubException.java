package com.letv.netty.rpc.core.error;

/**
 *
 * Description:
 *     callback已经失效的异常<br>
 *     Remove the stub object when get this Exception<br>
 *
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 */
public class CallbackStubException extends LedoException {


    protected CallbackStubException() {
    }

    private String errorMsg;

    public CallbackStubException(String errorMsg){
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public CallbackStubException(int code, String errorMsg) {
        super(code, errorMsg);
    }

    public CallbackStubException(int code, String errorMsg, Throwable throwable){
        super(code,errorMsg,throwable);
        this.errorMsg = errorMsg;

    }
}
