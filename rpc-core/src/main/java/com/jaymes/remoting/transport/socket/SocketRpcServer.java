package com.jaymes.remoting.transport.socket;

import com.elias.entity.RpcServiceProperties;
import com.elias.factory.SingletonFactory;
import com.elias.utils.ThreadPoolFactoryUtils;
import com.jaymes.config.CustomShutdownHook;
import com.jaymes.provider.ServiceProvider;
import com.jaymes.provider.ServiceProviderImpl;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 20:54
 */
@Slf4j
public class SocketRpcServer {

  private final Integer PORT = 9998;
  private final ExecutorService threadPool;
  private final ServiceProvider serviceProvider;

  public SocketRpcServer() {
    threadPool = ThreadPoolFactoryUtils
        .createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
    SingletonFactory.getInstance(ServiceProviderImpl.class);
    serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
  }

  public void registerService(Object service) {
    serviceProvider.publishService(service);
  }

  public void registerService(Object service,
      RpcServiceProperties rpcServiceProperties) {
    serviceProvider.publishService(service, rpcServiceProperties);
  }

  public void start() {
    try (ServerSocket server = new ServerSocket()) {
      String host = InetAddress.getLocalHost().getHostAddress();
      server.bind(new InetSocketAddress(host, PORT));
      CustomShutdownHook.getCustomShutdownHook().clearAll();
      Socket socket;
      while ((socket = server.accept()) != null) {
        log.info("client connected [{}]", socket.getInetAddress());
        threadPool.execute(new SocketRpcRequestHandler(socket));
      }
      threadPool.shutdown();
    } catch (IOException e) {
      log.error("occur IOException:", e);
    }
  }
}
