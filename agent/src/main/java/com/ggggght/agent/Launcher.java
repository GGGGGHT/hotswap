package com.ggggght.agent;

import com.ggggght.agent.enhancer.ASMEnhancer;
import com.ggggght.agent.enhancer.Enhancer;
import com.ggggght.agent.enhancer.JavassistEnhancer;
import java.lang.instrument.Instrumentation;

public class Launcher {
  private static final String DISPATCH_SERVLET =
      "org.springframework.web.servlet.DispatcherServlet";
  private static final String DISPATCH_HANDLER =
      "org.springframework.web.reactive.DispatcherHandler";

  public static void premain(String agentArgs, Instrumentation inst) {
    System.out.println("[Agent] In premain method");

    transformClass(DISPATCH_SERVLET, inst);
    // transformClass(DISPATCH_HANDLER, inst);

    System.out.println("[Agent] end");
  }

  private static void transformClass(String className, Instrumentation instrumentation) {
    Class<?> targetCls;
    ClassLoader targetClassLoader;
    // see if we can get the class using forName
    try {
      targetCls = Class.forName(className);
      targetClassLoader = targetCls.getClassLoader();
      transform(targetCls, targetClassLoader, instrumentation);
      return;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    // otherwise iterate all loaded classes and find what we want
    for(Class<?> clazz: instrumentation.getAllLoadedClasses()) {
      if(clazz.getName().equals(className)) {
        targetCls = clazz;
        targetClassLoader = targetCls.getClassLoader();
        transform(targetCls, targetClassLoader, instrumentation);
        return;
      }
    }
    throw new RuntimeException("Failed to find class [" + className + "]");
  }

  private static void transform(Class<?> clazz, ClassLoader classLoader, Instrumentation instrumentation) {
    // AtmTransformer dt = new AtmTransformer(clazz.getName(), classLoader);
    // ASMEnhancer asmEnhancer = new ASMEnhancer(clazz.getName(), classLoader);
    Enhancer enhancer = new JavassistEnhancer(clazz.getName(), classLoader);
    instrumentation.addTransformer(enhancer, true);
    try {
      instrumentation.retransformClasses(clazz);
    } catch (Exception ex) {
      throw new RuntimeException("Transform failed for class: [" + clazz.getName() + "]", ex);
    }
  }
}
