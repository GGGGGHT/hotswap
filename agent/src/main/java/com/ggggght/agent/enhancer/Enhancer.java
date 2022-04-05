package com.ggggght.agent.enhancer;

import java.lang.instrument.ClassFileTransformer;

/**
 * 字段码增强接口
 */
public interface Enhancer extends ClassFileTransformer {
  String DISPATCHER_SERVLET = "org.springframework.web.servlet.DispatcherServlet";
  String DISPATCHER_HANDLER = "org.springframework.web.reactive.DispatcherHandler";
  byte[] generate() throws Exception;
}
