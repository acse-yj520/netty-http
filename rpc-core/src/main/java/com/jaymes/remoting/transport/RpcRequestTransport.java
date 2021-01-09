package com.jaymes.remoting.transport;

import com.jaymes.extension.SPI;
import com.jaymes.remoting.dto.RpcRequest;


@SPI
public interface RpcRequestTransport {

  /**
   * send rpc request to server and get result
   *
   * @param rpcRequest message body
   * @return data from server
   */
  Object sendRpcRequest(RpcRequest rpcRequest);
}
