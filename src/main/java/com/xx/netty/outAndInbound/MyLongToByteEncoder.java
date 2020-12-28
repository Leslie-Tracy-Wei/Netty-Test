package com.xx.netty.outAndInbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Long aLong, ByteBuf byteBuf) throws Exception {
        System.out.println("encoder 方法被调用");
        System.out.println("long is " + aLong);
        byteBuf.writeLong(aLong);

    }
}
