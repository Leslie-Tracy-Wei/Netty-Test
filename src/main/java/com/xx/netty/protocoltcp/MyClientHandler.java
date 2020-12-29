package com.xx.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class MyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送速冻信息
        for (int i = 0; i < 10; i++) {
            String mess = "天气速冻";
            byte[] buffer = mess.getBytes(CharsetUtil.UTF_8);
            int length = buffer.length;
            ProtocolMessage message = new ProtocolMessage();
            message.setContent(buffer);
            message.setLen(length);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("cause" + cause.getMessage());
        ctx.close();
    }
}
