package com.xx.netty.tcp2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bo = new Bootstrap();
            bo.group(group).channel(NioSocketChannel.class).handler(new MyClientInitiallizer());

            ChannelFuture channelFuture = bo.connect("localhost", 8888).sync();
            channelFuture.channel().close().sync();
        }finally {
            group.shutdownGracefully();
        }

    }
}
