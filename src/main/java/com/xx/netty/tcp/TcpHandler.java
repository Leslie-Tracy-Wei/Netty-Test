package com.xx.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 说明：自定义一个handler需要继承某个HandlerAdapter
 * 这个时候，自定义的handler才能有对应属性 才能成为一个handler
 */
public class TcpHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读出数据的事件
     * 1.ChannelHandlerContext ctx 上下文对象 含有管道pipeline ，通道channel 地址
     * 2.Object msg 就是客户端发送过来的数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

//        System.out.println("server ctx = " + ctx);
//        // 将msg转成一个byteBuf
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline();
//        System.out.println("channel is -> " + channel + "\n" + " pipeline is ->" + pipeline);
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println("客户端发送的消息是:" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("地址为" + ctx.channel().remoteAddress());

        // 比如这里有一个非常耗时的业务 -》 异步执行 -> 提交该channel对应的NIOEventLoop的taskQueue中

        // 1.用户自定义普通任务解决
        ctx.channel().eventLoop().execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 111111",CharsetUtil.UTF_8));
                } catch (Exception e){
                    System.out.println("发生异常");
                }
            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 2222",CharsetUtil.UTF_8));
                } catch (Exception e){
                    System.out.println("发生异常");
                }
            }
        });

        // 用户自定义定时任务 -> 该任务提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 3333",CharsetUtil.UTF_8));
                } catch (Exception e){
                    System.out.println("发生异常");
                }
            }
        },5, TimeUnit.SECONDS);


    }

    /**
     * 数据读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 数据写入到缓存，并刷新
       ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端~",CharsetUtil.UTF_8));
    }

    /**
     * 处理异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.channel().close();
        ctx.close();
    }

}
