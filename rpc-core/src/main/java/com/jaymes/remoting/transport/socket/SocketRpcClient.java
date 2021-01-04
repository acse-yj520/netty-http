package com.jaymes.remoting.transport.socket;

import com.elias.entity.RpcServiceProperties;
import com.elias.exception.RpcException;
import com.elias.extension.ExtensionLoader;
import com.jaymes.registry.ServiceDiscovery;
import com.jaymes.remoting.dto.RpcRequest;
import com.jaymes.remoting.transport.ClientTransport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 17:08
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements ClientTransport {

  private final ServiceDiscovery serviceDiscovery;

  public SocketRpcClient() {
    this.serviceDiscovery = ExtensionLoader
        .getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
  }

  @Override
  public Object sendRpcRequest(RpcRequest rpcRequest) {
    // build rpc service name by rpcRequest
    String rpcServiceName = RpcServiceProperties.builder()
        .serviceName(rpcRequest.getInterfaceName())
        .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build()
        .toRpcServiceName();
    InetSocketAddress inetSocketAddress = serviceDiscovery
        .lookupService(rpcServiceName);
    try (Socket socket = new Socket()) {
      socket.connect(inetSocketAddress);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(
          socket.getOutputStream());
      // Send data to the server through the output stream
      objectOutputStream.writeObject(rpcRequest);
      ObjectInputStream objectInputStream = new ObjectInputStream(
          socket.getInputStream());
      // Read RpcResponse from the input stream
      return objectInputStream.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RpcException("调用服务失败:", e);
    }
  }
}
