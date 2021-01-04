package com.jaymes.config;

import com.elias.utils.ThreadPoolFactoryUtils;
import com.jaymes.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 22:01
 */
@Slf4j
public class CustomShutdownHook {

  private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

  public static CustomShutdownHook getCustomShutdownHook() {
    return CUSTOM_SHUTDOWN_HOOK;
  }

  public void clearAll() {
    log.info("addShutdownHook for clearAll");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
      ThreadPoolFactoryUtils.shutDownAllThreadPool();
    }));
  }
}
