package com.xx.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * httpObject 表示C端和服务器端相互通讯的数据封装成这个
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest){
            System.out.println("msg instanct " + msg.getClass());
            System.out.println(ctx.channel().remoteAddress() + " 地址");

            // 回复信息给浏览器 满足http协议
            ByteBuf content = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);

            // 构造一个http响应 即httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);
            response.headers().set("Content-Type","text/plain;charset=utf-8");
            response.headers().set("Content-Length",content.readableBytes());
            ctx.writeAndFlush(response);
        }
    }
}
