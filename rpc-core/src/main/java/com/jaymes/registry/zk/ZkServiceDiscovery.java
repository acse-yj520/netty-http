package com.jaymes.registry.zk;


import com.jaymes.enums.RpcErrorMessageEnum;
import com.jaymes.exception.RpcException;
import com.jaymes.extension.ExtensionLoader;
import com.jaymes.loadbalance.LoadBalance;
import com.jaymes.registry.ServiceDiscovery;
import com.jaymes.registry.zk.util.CuratorUtils;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

/**
 * service discovery based on zookeeper
 *
 * @author shuang.kou
 * @createTime 2020年06月01日 15:16:00
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

  private final LoadBalance loadBalance;

  public ZkServiceDiscovery() {
    this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
        .getExtension("loadBalance");
  }

  @Override
  public InetSocketAddress lookupService(String rpcServiceName) {
    CuratorFramework zkClient = CuratorUtils.getZkClient();
    List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
    if (serviceUrlList == null || serviceUrlList.size() == 0) {
      throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
    }
    // load balancing
    String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceName);
    log.info("Successfully found the service address:[{}]", targetServiceUrl);
    String[] socketAddressArray = targetServiceUrl.split(":");
    String host = socketAddressArray[0];
    int port = Integer.parseInt(socketAddressArray[1]);
    return new InetSocketAddress(host, port);
  }
}
