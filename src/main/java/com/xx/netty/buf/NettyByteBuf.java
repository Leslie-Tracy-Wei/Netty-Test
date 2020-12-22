package com.xx.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyByteBuf {
    public static void main(String[] args) {

        ByteBuf buf = Unpooled.copiedBuffer("hello world!", CharsetUtil.UTF_8);
        if (buf.hasArray()){
            byte[] array = buf.array();

            String s = new String(array, Charset.forName("UTF-8"));
            System.out.println(s);
            System.out.println(buf);

            System.out.println(buf.arrayOffset());
//            System.out.println(buf.readByte());
//            System.out.println(buf.getByte(0)); // 不影响大小
            System.out.println(buf.readerIndex());
            System.out.println(buf.writerIndex());
            System.out.println(buf.capacity());
            System.out.println(buf.readableBytes()); // 当前可读的字节数


            for (int i = 0; i < buf.readableBytes(); i++) {
                System.out.println((char)buf.getByte(i));
            }

//            System.out.println(buf.getCharSequence());
        }
    }
}
