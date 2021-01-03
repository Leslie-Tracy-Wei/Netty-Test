package com.xx.netty.myDubboRPC.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    // 上线文
    private ChannelHandlerContext handlerContext;
    // 返回的结果
    private String result;
    // 请求的参数
    private String param;



    // 与服务器创立连接的时候 调用 1
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handlerContext = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    // 收到服务器数据后，调用 4
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result = msg.toString();
        // 唤醒等待的线程
        notify();
    }

    // 被代理对象调用 发送数据给服务器 -> wait -> 等待被唤起 -> 返回数据 3
    @Override
    public synchronized Object call() throws Exception {
        handlerContext.writeAndFlush(param);
        // wait 等待channelRead 方法获取到服务器的结果后 唤醒
        wait();
        return result; // 服务方返回的结果
    }

    // 2
    public void setParam(String param) {
        this.param = param;
    }
}
