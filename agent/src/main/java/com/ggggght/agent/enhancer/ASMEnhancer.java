package com.ggggght.agent.enhancer;

import java.io.IOException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.ClassWriter.*;
import static org.objectweb.asm.Opcodes.*;

public class ASMEnhancer implements Enhancer {
  @Override public byte[] generate() throws IOException {
    return new byte[0];
  }

  private String targetClassName;
  /**
   * The class loader of the class we want to transform
   */
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
    if (!className.equals(finalTargetClassName) || loader != targetClassLoader) {
      return byteCode;
    }

    ClassReader cr = new ClassReader(byteCode);
    ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
    cr.accept(new MyClassAdapter(cw), ClassReader.EXPAND_FRAMES);
    return cw.toByteArray();
  }
}

class MyClassAdapter extends ClassVisitor {
  public MyClassAdapter(ClassVisitor cv) {
    super(ASM5, cv);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
      String[] exceptions) {
    if (!name.equals("doDispatch")) {
      return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    return new MyMethodVisitor(access, name, descriptor, mv);
  }
}

class MyMethodVisitor extends AdviceAdapter {

  public MyMethodVisitor(int access,
      String name,
      String desc,
      MethodVisitor mv) {
    super(ASM9, mv, access, name, desc);
  }

  @Override protected void onMethodEnter() {
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    mv.visitLdcInsn("enter");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
        false);
  }

  @Override protected void onMethodExit(int opcode) {
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
    mv.visitLdcInsn("exit");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
        false);
  }
}

