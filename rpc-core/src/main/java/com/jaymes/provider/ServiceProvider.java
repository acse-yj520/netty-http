package com.jaymes.provider;


import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.extension.SPI;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 17:27
 */
@SPI
public interface ServiceProvider {

  /**
   * @param service              service object
   * @param serviceClass         the interface class implemented by the service
   *                             instance object
   * @param rpcServiceProperties service related attributes
   */
  void addService(Object service, Class<?> serviceClass,
      RpcServiceProperties rpcServiceProperties);

  /**
   * 获取服务
   *
   * @param rpcServiceProperties service related attributes
   * @return service object
   */
  Object getService(RpcServiceProperties rpcServiceProperties);

  /**
   * @param service              service object
   * @param rpcServiceProperties service related attributes
   */
  void publishService(Object service,
      RpcServiceProperties rpcServiceProperties);

  /**
   * @param service service object
   */
  void publishService(Object service);
}
