package registry.zk;


import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.ServiceRegistry;
import registry.zk.util.CuratorUtils;

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
