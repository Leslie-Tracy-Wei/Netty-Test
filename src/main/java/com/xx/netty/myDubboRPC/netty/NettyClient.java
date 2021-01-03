package com.xx.netty.myDubboRPC.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    // 创建一个线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientHandler;

    // 使用代理模式 获取代理对象
    public Object getBean(final Class<?> serviceClass,final String protocol){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class<?>[] {serviceClass},
                (proxy, method, args) -> {

            // 客户端每调用一次都要执行一次
            if (clientHandler == null){
                initClient();
            }
            // 设置要发送的服务端的消息
            // protocol 对应的协议 args参数
            clientHandler.setParam(protocol + args[0]);
            return executor.submit(clientHandler).get();
        });
    }
    // 初始化客户端
    private static void initClient() throws Exception{
        clientHandler = new NettyClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(clientHandler);
            }
        });
        ChannelFuture channelFuture = b.connect("localhost", 9999).sync();
        channelFuture.channel().closeFuture();
    }
}
