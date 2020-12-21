package com.xx.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClient {

    public static void main(String[] args) throws Exception {
        // Client只需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        // C端需要的是Bootstrap not  ServerC端需要的是Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        try{
            bootstrap.group(eventExecutors) // 设置线程组
                    .channel(NioSocketChannel.class) // 实现的通道class
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TcpClientHandler());
                        }
                    });

            System.out.println("init client finish");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }

    }
}
