package com.ggggght.retransform.enhancer;

/**
 * 字段码增强接口
 */
public interface Enhancer {
  byte[] generate() throws Exception;
}
