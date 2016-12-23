package com.letv.netty.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
public class SimpleChatClient {
    private final String host;
    private final int port;

    public SimpleChatClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public Channel run() throws Exception{
    	EventLoopGroup group = new NioEventLoopGroup();
        Channel channel = null;
        try {
            Bootstrap bootstrap  = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                    .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .handler(new SimpleChatClientInitializer());
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            
            System.out.println("客户端已经连接上！");

            channelFuture.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                channel = channelFuture.channel();
            }else{
            	System.out.println("Failed to connect " + host + ":" + port);
            }
            /*while(true){
                channel.writeAndFlush("i am client ! \n" );
                Thread.sleep(3000);
                System.out.println("客户端向服务器发了消息");
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return  channel;
    }

}