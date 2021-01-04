package com.jaymes.remoting.transport;

import com.jaymes.remoting.dto.RpcRequest;


public interface ClientTransport {

  /**
   * send rpc request to server and get result
   *
   * @param rpcRequest message body
   * @return data from server
   */
  Object sendRpcRequest(RpcRequest rpcRequest);
}
