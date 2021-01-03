package com.xx.netty.myDubboRPC.customer;

import com.xx.netty.myDubboRPC.netty.NettyClient;
import com.xx.netty.myDubboRPC.normal.HelloService;

public class ClientBootstrap {
    private static final String PROTOCOL = "HELLO#";
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        HelloService helloService = (HelloService)nettyClient.getBean(HelloService.class,PROTOCOL);
        String result = helloService.hello("你好 dubbo");

        System.out.println("result = " + result);

    }
}
