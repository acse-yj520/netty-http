package com.jaymes.client;


import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.proxy.RpcClientProxy;
import com.jaymes.remoting.transport.ClientTransport;
import com.jaymes.remoting.transport.socket.SocketRpcClient;
import com.jaymes.service.Hello;
import com.jaymes.service.HelloService;


/**
 * @author Jaymes Yao
 * @date 2021/1/4 22:06
 */
public class RpcFrameworkSimpleClientMain {

  public static void main(String[] args) {
    ClientTransport clientTransport = new SocketRpcClient();
    RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
        .group("test2").version("version2").build();
    RpcClientProxy rpcClientProxy = new RpcClientProxy(clientTransport,
        rpcServiceProperties);
    HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
    String hello = helloService.hello(new Hello("111", "222"));
    System.out.println(hello);
  }
}
