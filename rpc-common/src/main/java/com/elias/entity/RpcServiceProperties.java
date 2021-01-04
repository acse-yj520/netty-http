package com.elias.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 16:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {

  /**
   * service version
   */
  private String version;
  /**
   * when the interface has multiple implementation classes, distinguish by
   * group
   */
  private String group;
  private String serviceName;

  public String toRpcServiceName() {
    return this.getServiceName() + this.getGroup() + this.getVersion();
  }
}
