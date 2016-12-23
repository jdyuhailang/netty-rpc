package com.letv.netty.rpc.core.error;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>吴涛</a>
 * <br/>
 * @Date: 2016/08/22 16:54
 */
public class InitErrorException extends LedoException {


    protected InitErrorException() {

    }

    public InitErrorException(String errorMsg, Throwable e) {
        super(e);
        this.errorMsg = errorMsg;
    }

    public InitErrorException(String errorMsg) {
        super(errorMsg);
    }

    public InitErrorException(int code,String errorMsg,Throwable e){
        super(code,errorMsg,e);

    }

     public InitErrorException(int code,String errorMsg){
         super(code,errorMsg);
    }




}
