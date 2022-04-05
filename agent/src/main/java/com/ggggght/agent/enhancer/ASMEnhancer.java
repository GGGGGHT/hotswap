package com.ggggght.agent.enhancer;

import java.io.IOException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.ClassWriter.*;
import static org.objectweb.asm.Opcodes.*;

public class ASMEnhancer implements Enhancer {
  @Override public byte[] generate() throws IOException {
    ClassReader cr = new ClassReader(DISPATCHER_SERVLET);
    ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);

    MethodVisitor method = cw.visitMethod(ACC_PROTECTED,
        "doDispatch",
        "(Ljavax.servlet.http.HttpServletRequest;Ljavax.servlet.http.HttpServletResponse;)V",
        null,
        new String[] {Exception.class.getName()});

    // add information when method called
    {
      // System.out.println
      method.visitFieldInsn(GETSTATIC,"java.lang.System","out","Ljava.io.PrintStream;");
      method.visitVarInsn(ALOAD, 1);
      method.visitMethodInsn(INVOKEINTERFACE, "javax.servlet.http.HttpServletRequest",
          "getRequestURI", "()Ljava/lang/String;", false);
      method.visitMethodInsn(INVOKEVIRTUAL, "java.io.PrintStream", "println",
          "(Ljava.lang.String;)V", false);
      method.visitFieldInsn(GETSTATIC,"java.lang.System","out","Ljava.io.PrintStream;");
      method.visitLdcInsn("============================");
      method.visitMethodInsn(INVOKEVIRTUAL, "java.io.PrintStream", "println",
          "(Ljava.lang.String;)V", false);
    }

    return cw.toByteArray();
  }

  private String targetClassName;
  /** The class loader of the class we want to transform */
  private ClassLoader targetClassLoader;

  public ASMEnhancer(String targetClassName, ClassLoader targetClassLoader) {
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
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return byteCode;
  }

}
