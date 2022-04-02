package com.ggggght.retransform.enhancer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class JavassistEnhancer implements Enhancer {
  public byte[] generate() throws NotFoundException, CannotCompileException {
    ClassPool classPool = ClassPool.getDefault();
    CtClass ctclass = classPool.makeClass("org.springframework.web.servlet.DispatcherServlet");
    CtMethod method = ctclass.getDeclaredMethod("doDispatcher");
    method.insertBefore("");
  }
}
