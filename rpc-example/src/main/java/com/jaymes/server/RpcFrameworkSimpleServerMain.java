package com.jaymes.server;

import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.remoting.transport.socket.SocketRpcServer;
import com.jaymes.service.HelloService;

/**
 * @author Jaymes Yao
 * @date 2021/1/5 14:46
 */
public class RpcFrameworkSimpleServerMain {

  public static void main(String[] args) {
    HelloService helloService = new HelloServiceImpl();
    SocketRpcServer socketRpcServer = new SocketRpcServer();
    RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
        .group("test2").version("version2").build();
    socketRpcServer.registerService(helloService, rpcServiceProperties);
    socketRpcServer.start();
  }

}
