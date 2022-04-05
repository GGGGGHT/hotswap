package com.ggggght.agent.enhancer;

import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

public class JavassistEnhancer implements Enhancer{
  public byte[] generate() throws Exception {
    ClassPool classPool = ClassPool.getDefault();
    CtClass ctclass = classPool.getCtClass(DISPATCHER_SERVLET);
    CtConstructor constructor = ctclass.getDeclaredConstructor(new CtClass[] {});
    constructor.insertBefore("System.out.println(\"call constructor method.\");");

    CtMethod method = ctclass.getDeclaredMethod("doDispatch");
    method.insertBefore("System.out.println(\"request.getMethod = \" + request.getMethod());");
    method.insertBefore("System.out.println(\"request.getRequestURL = \" + request.getRequestURL());");
    method.insertBefore("System.out.println(\"request.getAuthType = \" + request.getAuthType());");
    method.insertAfter("System.out.println(\"response.getStatus= \" + response.getStatus());");
    return ctclass.toBytecode();
  }

  private String targetClassName;
  /** The class loader of the class we want to transform */
  private ClassLoader targetClassLoader;

  public JavassistEnhancer(String targetClassName, ClassLoader targetClassLoader) {
    this.targetClassName = targetClassName;
    this.targetClassLoader = targetClassLoader;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer) {
    byte[] byteCode = classfileBuffer;
    String finalTargetClassName = this.targetClassName.replaceAll("\\.", "/"); //replace . with /
    if (!className.equals(finalTargetClassName)) {
      return byteCode;
    }

    if (loader.equals(targetClassLoader)) {

      try {
        byteCode = this.generate();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return byteCode;
  }

}
