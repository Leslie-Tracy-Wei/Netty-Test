package com.xx.netty.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost",8000));
        String fileName = "商家入驻重构需求1216R1.zip";
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();
        long start = System.currentTimeMillis();

        // linux transformTo可以传输完成
        // window transformTo只能传送8m 只能分段
        long count = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("file size : " + count);
        System.out.println("time :" + (System.currentTimeMillis() - start));

        fileChannel.close();
        socketChannel.close();
    }
}
