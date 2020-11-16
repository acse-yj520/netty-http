package registry.zk;


import com.elias.enums.RpcErrorMessageEnum;
import com.elias.exception.RpcException;
import com.elias.extension.ExtensionLoader;
import java.net.InetSocketAddress;
import java.util.List;
import loadbalance.LoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import registry.ServiceDiscovery;
import registry.zk.util.CuratorUtils;

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
