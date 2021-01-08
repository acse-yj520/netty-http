package com.jaymes.remoting.handler;


import com.jaymes.exception.RpcException;
import com.jaymes.factory.SingletonFactory;
import com.jaymes.provider.ServiceProvider;
import com.jaymes.provider.ServiceProviderImpl;
import com.jaymes.remoting.dto.RpcRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * RpcRequest processor 根据 rpc 请求调用目标类的目标方法
 *
 * @author Jaymes Yao
 * @date 2021/1/4 17:27
 */
@Slf4j
public class RpcRequestHandler {

  private final ServiceProvider serviceProvider;


  public RpcRequestHandler() {
    serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
  }

  /**
   * Processing rpcRequest: call the corresponding method, and then return the
   * method
   */
  public Object handle(RpcRequest rpcRequest) {
    Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
    return invokeTargetMethod(rpcRequest, service);
  }

  /**
   * get method execution results
   *
   * @param rpcRequest client request
   * @param service    service object
   * @return the result of the target method execution
   */
  private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
    Object result;
    try {
      Method method = service.getClass()
          .getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
      result = method.invoke(service, rpcRequest.getParameters());
      log.info("service:[{}] successful invoke method:[{}]",
          rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
    } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
      throw new RpcException(e.getMessage(), e);
    }
    return result;
  }

}
