package com.letv.netty.rpc.message;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:07
 */
public class ResponseMessage extends BaseMessage {
    private Object response;

    //error when the error has been declare in the interface
    private Throwable exception;

    public ResponseMessage(boolean initHeader) {
        super(initHeader);
    }

    public ResponseMessage() {
        super(true);
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return exception != null;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "header="+ getMsgHeader() +
                "response=" + response +
                ", exception=" + exception +
                '}';
    }
}
