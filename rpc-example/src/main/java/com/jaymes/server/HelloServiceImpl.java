package com.jaymes.server;

import com.jaymes.service.Hello;
import com.jaymes.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jaymes Yao
 * @date 2021/1/5 14:43
 */
@Slf4j
public class HelloServiceImpl implements HelloService {

  static {
    System.out.println("HelloServiceImpl被创建");
  }

  @Override
  public String hello(Hello hello) {
    log.info("HelloServiceImpl收到: {}.", hello.getMessage());
    String result = "Hello description is " + hello.getDescription();
    log.info("HelloServiceImpl返回: {}.", result);
    return result;
  }
}
