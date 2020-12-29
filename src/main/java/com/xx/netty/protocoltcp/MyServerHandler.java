package com.xx.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private int count ;
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
        byte[] content = msg.getContent();
        int len = msg.getLen();
        System.out.println("服务器接收到消息如下");
        System.out.println("长度:" + len);
        System.out.println("内容:" + new String(content,CharsetUtil.UTF_8));
        System.out.println("服务器接收的包次数为 : " + (++ this.count));
    }
}
