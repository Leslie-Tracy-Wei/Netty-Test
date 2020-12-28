package com.xx.netty.outAndInbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Long aLong) throws Exception {
        System.out.println("服务器回送信息:" + channelHandlerContext.channel().remoteAddress() + " " + aLong);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("myclientHandler send info ");
        ctx.writeAndFlush(123456L);

        /**
         *          编写时，类型要一致 否则不会走encode方法
         *          if (acceptOutboundMessage(msg)) {
         *                 @SuppressWarnings("unchecked")
         *                 I cast = (I) msg;
         *                 buf = allocateBuffer(ctx, cast, preferDirect);
         *                 try {
         *                     encode(ctx, cast, buf);
         *                 } finally {
         *                     ReferenceCountUtil.release(cast);
         *                 }
         *
         *                 if (buf.isReadable()) {
         *                     ctx.write(buf, promise);
         *                 } else {
         *                     buf.release();
         *                     ctx.write(Unpooled.EMPTY_BUFFER, promise);
         *                 }
         *                 buf = null;
         *             } else {
         *                 ctx.write(msg, promise);
         *             }
         */
//        ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd",CharsetUtil.UTF_8));
    }
}
