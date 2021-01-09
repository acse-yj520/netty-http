package com.jaymes.spring;

import com.jaymes.entity.RpcServiceProperties;
import com.jaymes.extension.ExtensionLoader;
import com.jaymes.factory.SingletonFactory;
import com.jaymes.provider.ServiceProvider;
import com.jaymes.provider.ServiceProviderImpl;
import com.jaymes.proxy.RpcClientProxy;
import com.jaymes.remoting.transport.RpcRequestTransport;
import com.jaymes.spring.annotation.RpcReference;
import com.jaymes.spring.annotation.RpcService;
import java.lang.reflect.Field;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * call this method before creating the bean to see if the class is annotated
 *
 * @author shuang.kou
 * @createTime 2020年07月14日 16:42:00
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

  private final ServiceProvider serviceProvider;
  private final RpcRequestTransport rpcClient;

  public SpringBeanPostProcessor() {
    this.serviceProvider = SingletonFactory
        .getInstance(ServiceProviderImpl.class);
    this.rpcClient = ExtensionLoader
        .getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
  }

  @SneakyThrows
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean.getClass().isAnnotationPresent(RpcService.class)) {
      log.info("[{}] is annotated with  [{}]", bean.getClass().getName(),
          RpcService.class.getCanonicalName());
      // get RpcService annotation
      RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
      // build RpcServiceProperties
      RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
          .group(rpcService.group()).version(rpcService.version()).build();
      serviceProvider.publishService(bean, rpcServiceProperties);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> targetClass = bean.getClass();
    Field[] declaredFields = targetClass.getDeclaredFields();
    for (Field declaredField : declaredFields) {
      RpcReference rpcReference = declaredField
          .getAnnotation(RpcReference.class);
      if (rpcReference != null) {
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties
            .builder()
            .group(rpcReference.group()).version(rpcReference.version())
            .build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient,
            rpcServiceProperties);
        Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
        declaredField.setAccessible(true);
        try {
          declaredField.set(bean, clientProxy);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }

    }
    return bean;
  }
}
