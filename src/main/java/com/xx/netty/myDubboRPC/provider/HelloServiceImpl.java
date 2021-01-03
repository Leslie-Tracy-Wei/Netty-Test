package com.xx.netty.myDubboRPC.provider;

import com.xx.netty.myDubboRPC.normal.HelloService;

public class HelloServiceImpl implements HelloService {

    // 当有消费方调用该方法是 ，就返回一个结果
    @Override
    public String hello(String msg) {
        System.out.println("接收到客户端发送的消息: " + msg);
        if (msg != null){
            return "服务端返回到客户端的消息是: " + msg;
        }
        return null;
    }
}
