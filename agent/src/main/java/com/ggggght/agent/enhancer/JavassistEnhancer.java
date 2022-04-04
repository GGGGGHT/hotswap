package com.ggggght.agent.enhancer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

public class JavassistEnhancer implements Enhancer {
  public byte[] generate() throws Exception {
    ClassPool classPool = ClassPool.getDefault();
    CtClass ctclass = classPool.makeClass("org.springframework.web.servlet.DispatcherServlet");
    CtConstructor constructor = ctclass.getDeclaredConstructor(new CtClass[] {});
    constructor.insertBefore("System.out.println(\"call constructor method.\");");

    CtMethod method = ctclass.getDeclaredMethod("doDispatch");
    method.insertBefore("System.out.println(\"request.getMethod = \" + request.getMethod());");
    method.insertBefore("System.out.println(\"request.getRequestURL = \" + request.getRequestURL());");
    method.insertBefore("System.out.println(\"request.getAuthType = \" + request.getAuthType());");
    method.insertAfter("System.out.println(\"response.getStatus= \" + request.getStatus());");
    return ctclass.toBytecode();
  }
}
