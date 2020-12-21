package com.xx.netty.zerocopy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NewIOServer {
    public static void main(String[] args) throws Exception {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8000);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(inetSocketAddress);

        while (true){
            SocketChannel socket = serverSocketChannel.accept();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            int readCount = 0;
            while (-1 != readCount){
                readCount = socket.read(buffer);

                // 倒带 position为0 mark标志作废
                buffer.rewind();
            }
        }
    }
}
