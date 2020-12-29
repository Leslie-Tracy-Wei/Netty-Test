package com.xx.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyMessageDecoder extends ReplayingDecoder<ProtocolMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(" deocder 执行");
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        ProtocolMessage message = new ProtocolMessage();
        message.setLen(length);
        message.setContent(bytes);
    }
}
