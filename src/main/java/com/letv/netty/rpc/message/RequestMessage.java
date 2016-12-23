package com.letv.netty.rpc.message;


import com.letv.netty.rpc.spring.Constants;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:07
 */
public class RequestMessage  extends BaseMessage {
    private Invocation invocationBody;


    private long receiveTime;   //temp Property for Request receive time

    private String targetAddress; // Remote address
    public RequestMessage(boolean initHeader) {
        super(initHeader);
    }

    public RequestMessage() {
        super(true);
    }

    public Invocation getInvocationBody() {

        if(invocationBody == null) return null;

        if(invocationBody.getClazzName() == null){
            invocationBody.setClazzName(this.getClassName());
        }
        if(invocationBody.getMethodName() == null){
            invocationBody.setMethodName(this.getMethodName());
        }
        return invocationBody;
    }

    public Integer getClientTimeout(){
        return 1000;
    }

    public void setClientTimeout(long timeout){
        //this.getMsgHeader().addHeadKey(Constants.HeadKey.timeout,timeout);
    }

    public void setInvocationBody(Invocation invocationBody) {
        this.invocationBody = invocationBody;
    }

    public String getClassName(){
        return invocationBody.getClazzName();
    }

    public String getMethodName(){
        return invocationBody.getMethodName();
    }

    public String getAlias(){
        return invocationBody.getAlias();
    }

    public void setClassName(String className){
        invocationBody.setClazzName(className);
    }

    public void setMethodName(String methodName){
        invocationBody.setMethodName(methodName);

    }

    public void setAlias(String alias){
        invocationBody.setAlias(alias);
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }
}
