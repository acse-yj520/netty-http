package com.jaymes.client;


import com.jaymes.spring.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.jaymes"})
public class NettyClientMain {

  public static void main(String[] args) throws InterruptedException {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
        NettyClientMain.class);
    HelloController helloController = (HelloController) applicationContext
        .getBean("helloController");
    helloController.test();
  }
}
