package com.xx.netty.webSocket;

import com.xx.netty.heartBeat.HeartBeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyWebSocketServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))// 在bossGroup增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 因为是http协议，使用http的编解码器
                            pipeline.addLast("httpCodec", new HttpServerCodec());
                            // 是以块方式写，添加ChunkedWriteHandler()
                            pipeline.addLast("chunkedWriteHandler", new ChunkedWriteHandler());
                            // http传输过程中是分段的，HttpObjectAggregator可以将多个段聚合
                            // 这就是为什么 ，当浏览器发送大量数据时，就会发生多次http请求
                            pipeline.addLast(new HttpObjectAggregator(8196));

                            // 对于webSocket是以帧的形式传递的 frame
                            // WebSocketFrame下面有6个子类
                            // 浏览器请求时 ws://localhost:7000/xxx 表示请求的uri
                            // WebSocketServerProtocolHandler 核心功能将http协议升级为ws协议 ，保持长连接
                            // 通过一个状态码101 来转换的 200 -》 101
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 处理对应业务
                            pipeline.addLast(new MyHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
