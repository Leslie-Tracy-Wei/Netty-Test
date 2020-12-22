package com.xx.netty.groupChat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupChatClient {
    private final int port;

    private final String host;

    public GroupChatClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception{
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        try{
            b.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder",new StringDecoder())
                                    .addLast("encoder",new StringEncoder())
                                    .addLast("myHandler",new GroupChatClientHandler() );
                        }
                    });
            ChannelFuture channelFuture = b.connect(host, port).sync();
            System.out.println("--------" + channelFuture.channel().localAddress() + "-------------");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                // 通过channel发送到服务器
                channelFuture.channel().writeAndFlush(msg + "\r\n");
            }
        }finally {
            eventExecutors.shutdownGracefully();
        }

    }

    public static void main(String[] args) {

        try {
            new GroupChatClient("localhost",8888).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
