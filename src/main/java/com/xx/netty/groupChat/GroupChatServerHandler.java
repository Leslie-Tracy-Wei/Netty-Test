package com.xx.netty.groupChat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    // 定义一个channel组 ，管理所有的channel
    // GlobalEventExecutor.INSTANCE 全局的事件执行器 单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
        final Channel channel = ctx.channel();
        // 根据不同的情况，回送不同的消息
        channelGroup.forEach(ch -> {
            if (ch != channel){ // 不是当前的channel 转发消息
                ch.writeAndFlush("当前时间:" + dateFormat.format(new Date(System.currentTimeMillis())));
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息 " + msg + "\n");
            }else{
                ch.writeAndFlush("[自己发送了]" + msg + "\n");
            }
        });
    }

    // 表示连接建立 ，一旦连接，第一个被执行
    // 将当前的channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 该方法会将所有的channel遍历并发送 无须在遍历
        channelGroup.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() + "加入聊天\n");
        // 将该客户加入聊天的信息推送到其他在线的客户
        channelGroup.add(ctx.channel());
    }

    // 断开连接 将xx客户端离开信息推送给当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("客户端" + ctx.channel().remoteAddress() + "离开了\n");
    }

    // 表示channel处于活动的状态 提示上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了");
    }

    // 表示下线 处于非活动状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 下线了");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
