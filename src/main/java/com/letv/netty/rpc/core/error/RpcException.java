package com.letv.netty.rpc.core.error;


import com.letv.netty.rpc.message.MessageHeader;

/**
 * Description: <br/>
 *  远程调用执行时抛出的异常
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/22 17:44
 */
public class RpcException extends LedoException{


    private transient MessageHeader msgHeader;

    // 需要序列化支持
    protected RpcException() {
    }


    public RpcException(MessageHeader header, Throwable e) {
        super(e);
        this.msgHeader = header;

    }

    public RpcException(MessageHeader header, String errorMsg) {
        super(errorMsg);
        this.msgHeader = header;
        this.errorMsg = errorMsg;
    }

    protected RpcException(Throwable e) {
        super(e);
    }

    public RpcException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public RpcException(String errorMsg, Throwable e) {
        super(e);
        this.errorMsg = errorMsg;
    }

    public RpcException(int code, String errorMsg) {
        super(code, errorMsg);
    }

    public RpcException(int code, String errorMsg, Throwable e) {
        super(code, errorMsg, e);
    }

    public MessageHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MessageHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    public String toString() {
        String s = getClass().getName();
        String message = this.errorMsg;
        return (message != null) ? (s + ": " + message) : s;
    }
}
