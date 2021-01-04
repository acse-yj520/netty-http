package com.jaymes.provider;

import com.elias.entity.RpcServiceProperties;
import com.elias.extension.ExtensionLoader;
import com.jaymes.registry.ServiceRegistry;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 17:36
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

  private final Map<String, Object> serviceMap;
  private final Set<String> registeredService;
  private final ServiceRegistry serviceRegistry;

  public ServiceProviderImpl() {
    serviceMap = new ConcurrentHashMap<>();
    registeredService = ConcurrentHashMap.newKeySet();
    serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class)
        .getExtension("zk");
  }

  @Override
  public void addService(Object service, Class<?> serviceClass,
      RpcServiceProperties rpcServiceProperties) {
    // 获得服务名称
    String rpcServiceName = rpcServiceProperties.toRpcServiceName();
    if (registeredService.contains(rpcServiceName)) {
      return;
    }
    registeredService.add(rpcServiceName);
    serviceMap.put(rpcServiceName, service);
    log.info("Add service: {} and interfaces:{}", rpcServiceName,
        service.getClass().getInterfaces());
  }

  @Override
  public Object getService(RpcServiceProperties rpcServiceProperties) {
    return null;
  }

  @Override
  public void publishService(Object service,
      RpcServiceProperties rpcServiceProperties) {

  }

  @Override
  public void publishService(Object service) {

  }
}
