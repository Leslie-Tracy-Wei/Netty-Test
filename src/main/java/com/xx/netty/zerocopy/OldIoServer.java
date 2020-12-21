package com.xx.netty.zerocopy;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

// java io 服务器端

public class OldIoServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(7000);
        while (true){
            Socket accept = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(accept.getInputStream());
            byte[] bytes = new byte[4096];
            while (true){
                int count = dataInputStream.read(bytes,0,bytes.length);
                if (count == -1){
                    break;
                }
            }
        }
    }
}
