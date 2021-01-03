package com.xx.netty.myDubboRPC.provider;

import com.xx.netty.myDubboRPC.netty.NettyServer;

// 启动一个netty
public class ServerBootstrap {
    public static void main(String[] args) {
        NettyServer.startServer("localhost",9999);
    }
}
