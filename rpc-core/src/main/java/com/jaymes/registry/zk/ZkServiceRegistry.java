package com.jaymes.registry.zk;


import com.jaymes.registry.ServiceRegistry;
import com.jaymes.registry.zk.util.CuratorUtils;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

/**
 * service registration  based on zookeeper
 *
 * @author shuang.kou
 * @createTime 2020年05月31日 10:56:00
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

  @Override
  public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
    String servicePath =
        CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
    CuratorFramework zkClient = CuratorUtils.getZkClient();
    CuratorUtils.createPersistentNode(zkClient, servicePath);
  }
}
