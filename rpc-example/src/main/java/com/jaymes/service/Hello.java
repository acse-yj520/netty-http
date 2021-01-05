package com.jaymes.service;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Jaymes Yao
 * @date 2021/1/4 22:09
 */
@Data
@AllArgsConstructor
public class Hello implements Serializable {

  private String message;
  private String description;
}
