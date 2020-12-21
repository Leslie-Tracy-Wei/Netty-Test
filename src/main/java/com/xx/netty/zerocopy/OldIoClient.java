package com.xx.netty.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class OldIoClient {
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost",7000);
        String fileName = "商家入驻重构需求1216R1.zip";
        InputStream inputStream = new FileInputStream(fileName);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] bytes = new byte[4096];
        long readCount = 0;
        long total = 0;

        long startTime = System.currentTimeMillis();
        while ((readCount = inputStream.read(bytes)) >= 0){
            total += readCount;
            dataOutputStream.write(bytes);
        }

        System.out.println("total size " + total);
        System.out.println("total time : " + ( System.currentTimeMillis() - startTime) + "ms");
        dataOutputStream.close();
        socket.close();
        inputStream.close();
    }
}
