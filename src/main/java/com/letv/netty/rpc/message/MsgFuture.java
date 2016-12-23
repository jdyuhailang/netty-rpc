package com.letv.netty.rpc.message;


import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
public interface MsgFuture<V> extends Future<V> {

    V get(long timeout, TimeUnit unit) throws InterruptedException;

    boolean isSuccess();
    Throwable cause();
    V getNow();

    void releaseIfNeed();

    MsgFuture<V> setSuccess(V result);

    MsgFuture<V> setFailure(Throwable cause);

    /**
     * 异步调用listener方式
     *
     * @param listener
     *
     * @return
     */
    //MsgFuture addListener(ResultListener listener);

    /**
     * 异步调用ResponseFuture方式
     *
     * responseFuture
     * @return
     */
    //MsgFuture bindResponseFuture(ResponseFuture responseFuture);


    long getGenTime();


    int getTimeout();


    void setSentTime(long sentTime);


    boolean isAsyncCall();


    /**
     *
     * @param asyncCall
     *         是否异步调用
     */
    void setAsyncCall(boolean asyncCall);


    void setProviderLedoVersion(Short version);


}
