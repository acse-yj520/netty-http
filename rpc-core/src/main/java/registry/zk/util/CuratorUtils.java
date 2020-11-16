package registry.zk.util;


import com.elias.enums.RpcConfigEnum;
import com.elias.utils.PropertiesFileUtil;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Curator(zookeeper client) utils
 *
 * @author shuang.kou
 * @createTime 2020年05月31日 11:38:00
 */
@Slf4j
public final class CuratorUtils {

  public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
  /**
   * baseSleepTimeMs：重试之间等待的初始时间 maxRetries ：最大重试次数 connectString ：要连接的服务器列表 retryPolicy ：重试策略
   */
  private static final int BASE_SLEEP_TIME = 1000;
  private static final int MAX_RETRIES = 3;
  private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
  private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
  private static CuratorFramework zkClient;
  private static String defaultZookeeperAddress = "127.0.0.1:2181";


  private CuratorUtils() {
  }

  /**
   * Create persistent nodes. Unlike temporary nodes, persistent nodes are not removed when the
   * client disconnects
   *
   * @param path node path
   */
  public static void createPersistentNode(CuratorFramework zkClient, String path) {
    try {
      if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
        log.info("The node already exists. The node is:[{}]", path);
      } else {
        //eg: /my-rpc/github.javaguide.HelloService/127.0.0.1:9999
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        log.info("The node was created successfully. The node is:[{}]", path);
      }
      REGISTERED_PATH_SET.add(path);
    } catch (Exception e) {
      log.error("create persistent node for path [{}] fail", path);
    }
  }

  /**
   * Gets the children under a node
   *
   * @param rpcServiceName rpc service name eg:github.javaguide.HelloServicetest2version1
   * @return All child nodes under the specified node
   */
  public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
    if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
      return SERVICE_ADDRESS_MAP.get(rpcServiceName);
    }
    List<String> result = null;
    String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
    try {
      result = zkClient.getChildren().forPath(servicePath);
      SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
      registerWatcher(rpcServiceName, zkClient);
    } catch (Exception e) {
      log.error("get children nodes for path [{}] fail", servicePath);
    }
    return result;
  }

  /**
   * Empty the registry of data
   */
  public static void clearRegistry(CuratorFramework zkClient) {
    REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
      try {
        zkClient.delete().forPath(p);
      } catch (Exception e) {
        log.error("clear registry for path [{}] fail", p);
      }
    });
    log.info("All registered services on the server are cleared:[{}]",
        REGISTERED_PATH_SET.toString());
  }

  public static CuratorFramework getZkClient() {
    // check if user has set zk address
    Properties properties = PropertiesFileUtil
        .readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
    if (properties != null) {
      defaultZookeeperAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue());
    }
    // if zkClient has been started, return directly
    if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
      return zkClient;
    }
    // 通过 CuratorFrameworkFactory 创建 CuratorFramework 对象
    // 然后再调用  CuratorFramework 对象的 start() 方法即可
    // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
    zkClient = CuratorFrameworkFactory.builder()
        // the server to connect to (can be a server list)
        .connectString(defaultZookeeperAddress)
        .retryPolicy(retryPolicy)
        .build();
    zkClient.start();
    return zkClient;
  }

  /**
   * Registers to listen for changes to the specified node 给某个节点注册子节点监听器
   *
   * @param rpcServiceName rpc service name eg:github.javaguide.HelloServicetest2version
   */
  private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient)
      throws Exception {
    String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
    PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
    PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
      // 获取某个节点的所有子节点路径
      List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
      SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
    };
    pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
    pathChildrenCache.start();
  }

}