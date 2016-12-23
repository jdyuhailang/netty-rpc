package com.letv.netty.rpc.core.error;

import static com.letv.netty.rpc.utils.Constants.GOV_ERROR_RESOURCE;
import static com.letv.netty.rpc.utils.Constants.GOV_HOST;

/**
 * Description: <br/>
 * <p/>
 * Company: <a href=www.le.com>LeEco</a><br/>
 *
 * @author: <a href=mailto:wutao7@le.com>wutao</a>
 * <br/>
 * @Date: 2016/12/14 16:05
 */
public class LedoException extends RuntimeException {

    protected int code;

    protected String errorMsg;


    public LedoException() {

    }

    public LedoException(Throwable cause) {
        super(cause);
    }

    public LedoException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }



    public LedoException(int code,String errorMsg,Throwable e){
        super(String.format("[Ledo-%s] %s.%s",code,errorMsg,getErrorCodeResource(code)),e);
        this.code = code;
        this.errorMsg = errorMsg;

    }

     public LedoException(int code,String errorMsg){
        super(String.format("[Ledo-%s] %s %s",code,errorMsg,getErrorCodeResource(code)));
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public static String getErrorCodeResource(int code){
        return String.format(".View:%s/%s#Error-%s",GOV_HOST,GOV_ERROR_RESOURCE,code);
    }
}
