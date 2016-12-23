package com.letv.netty.rpc.client;


import com.letv.netty.rpc.message.Invocation;
import com.letv.netty.rpc.message.RequestMessage;
import com.letv.netty.rpc.utils.PooledBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class SimpleClientMain {

		 public static void main(String[] args) throws Exception{
			 Channel channel =  new SimpleChatClient("10.59.14.147", 21600).run();
			/* RequestMessage msg;
			 Invocation invocation = msg.getInvocationBody();
			 String methodName = invocation.getMethodName();

			 ByteBuf byteBuf = PooledBufHolder.getBuffer();
			 byte[] invocationData = codec.encode(msg.getInvocationBody());
			 byteBuf = byteBuf.writeBytes(invocationData);*/
			 channel.writeAndFlush("djaskldsa");
		    }
}
