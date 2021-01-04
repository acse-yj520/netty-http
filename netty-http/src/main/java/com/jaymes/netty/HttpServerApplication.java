package com.jaymes.netty;

import com.jaymes.netty.server.HttpServer;

public class HttpServerApplication {

  public static void main(String[] args) {
    HttpServer httpServer = new HttpServer();
    httpServer.start();
  }
}