package com.xx.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 加入netty提供的httpServerCodec 编解码器
        pipeline.addLast("myHttpServerCodec",new HttpServerCodec());
        // 增加自定义的handler
        pipeline.addLast("myHttpServerHandler",new HttpServerHandler());
    }
}
