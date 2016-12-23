package com.letv.netty.rpc.server;

import com.letv.netty.rpc.message.BaseMessage;
import com.letv.netty.rpc.message.ResponseMessage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: yuhailang
 * Date: 2016/12/21
 * Time: 15:15
 */
public interface Invoker {

    ResponseMessage invoke(BaseMessage msg);
}
