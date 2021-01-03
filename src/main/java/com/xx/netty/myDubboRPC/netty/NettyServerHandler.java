package com.xx.netty.myDubboRPC.netty;

import com.xx.netty.myDubboRPC.provider.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final String PROTOCOL = "HELLO#";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("服务端接收到的消息=" + msg );
        // 与客户端定义协议
        if (msg.toString().startsWith(PROTOCOL)){
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        } else {
            System.out.println("协议不一致，调用失败");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
