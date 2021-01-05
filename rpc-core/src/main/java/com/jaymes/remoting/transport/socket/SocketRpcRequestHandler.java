package com.jaymes.remoting.transport.socket;

import com.jaymes.factory.SingletonFactory;
import com.jaymes.remoting.dto.RpcRequest;
import com.jaymes.remoting.dto.RpcResponse;
import com.jaymes.remoting.handler.RpcRequestHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

/**
 * Socket 请求具体处理类
 *
 * @author Jaymes Yao
 * @date 2021/1/4 21:07
 */
@Slf4j
public class SocketRpcRequestHandler implements Runnable {

  private final Socket socket;
  private final RpcRequestHandler rpcRequestHandler;


  public SocketRpcRequestHandler(Socket socket) {
    this.socket = socket;
    this.rpcRequestHandler = SingletonFactory
        .getInstance(RpcRequestHandler.class);
  }

  @Override
  public void run() {
    log.info("server handle message from client by thread: [{}]",
        Thread.currentThread().getName());
    try (ObjectInputStream objectInputStream = new ObjectInputStream(
        socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
            socket.getOutputStream())) {
      RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
      Object result = rpcRequestHandler.handle(rpcRequest);
      objectOutputStream
          .writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
      objectOutputStream.flush();
    } catch (IOException | ClassNotFoundException e) {
      log.error("occur exception:", e);
    }
  }
}
