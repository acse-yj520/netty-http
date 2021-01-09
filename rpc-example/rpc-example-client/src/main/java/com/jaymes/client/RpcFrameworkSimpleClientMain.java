package com.jaymes.client;


import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.proxy.RpcClientProxy;
import com.jaymes.remoting.transport.RpcRequestTransport;
import com.jaymes.remoting.transport.socket.SocketRpcRequest;
import com.jaymes.service.Hello;
import com.jaymes.service.HelloService;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 22:06
 */
public class RpcFrameworkSimpleClientMain {

  public static void main(String[] args) {
    RpcRequestTransport rpcRequestTransport = new SocketRpcRequest();
    RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
        .group("test2").version("version2").build();
    RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport,
        rpcServiceProperties);
    HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
    String hello = helloService.hello(new Hello("111", "222"));
    System.out.println(hello);
  }
}
