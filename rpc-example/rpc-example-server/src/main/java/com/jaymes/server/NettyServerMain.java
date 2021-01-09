package com.jaymes.server;

import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.remoting.transport.netty.server.NettyRpcServer;
import com.jaymes.service.HelloService;
import com.jaymes.spring.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server: Automatic registration service via @RpcService annotation
 *
 * @author shuang.kou
 * @createTime 2020年05月10日 07:25:00
 */
@RpcScan(basePackage = {"com.jaymes"})
public class NettyServerMain {

  public static void main(String[] args) {
    // Register service via annotation
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
        NettyServerMain.class);
    NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext
        .getBean("nettyRpcServer");
    // Register service manually
    HelloService helloService2 = new HelloServiceImpl2();
    RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
        .group("test2").version("version2").build();
    nettyRpcServer.registerService(helloService2, rpcServiceProperties);
    nettyRpcServer.start();
  }
}
