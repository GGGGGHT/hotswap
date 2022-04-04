package com.ggggght.retransform;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.WebApplicationType;

public final class Utils {
  public static void retransform() {

  }

  /**
   * get current machine all java process id
   */
  public static List<Long> getAllJavaProcessId() {
    return VirtualMachine.list()
        .stream()
        .map(VirtualMachineDescriptor::id)
        .map(Long::valueOf)
        .collect(Collectors.toList());
  }

  /**
   * get special project pid
   * @param projectName
   * @return pid
   */
  public static Long getCurrentProjectPid(@NotNull String projectName) {
    return VirtualMachine.list().stream()
        .filter(virtualMachineDescriptor -> virtualMachineDescriptor.displayName()
            .contains(projectName))
        .findFirst()
        .map(virtualMachineDescriptor -> Long.valueOf(virtualMachineDescriptor.id()))
        .orElse(-1L);
  }

  /**
   * return current module used jdk version
   *
   * @param module 当前模块
   * @return jdkVersion
   */
  public static String getJdkVersion(@NotNull Module module) {
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    Sdk SDK = moduleRootManager.getSdk();

    return Objects.isNull(SDK) ? "" : SDK.getVersionString();
  }

  /**
   * 获取当前模块使用的jdk的家目录
   *
   * @param module 当前模块
   * @return jdkHomePath
   */
  public static String getJdkHomePath(@NotNull Module module) {
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    Sdk SDK = moduleRootManager.getSdk();

    return Objects.isNull(SDK) ? "" : SDK.getHomePath();
  }

  /**
   * return all dependencies
   *
   * @param module 当前模块
   * @return List<String>
   */
  public static Set<String> getAllDependencies(@NotNull Module module) {
    HashSet<String> libraries = new HashSet<>();
    ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(library -> {
      libraries.add(library.getName());
      return true;
    });

    return libraries;
  }

  /**
   * 判断当前项目是否是web项目
   * @param module 当前模块
   * @return isWebModule
   */
  public static WebApplicationType isWebModule(@NotNull Module module) {
    Set<String> dependencies = getAllDependencies(module);
    if (dependencies.contains("spring-boot-starter-web")) {
      return WebApplicationType.SERVLET;
    }

    if (dependencies.contains("spring-boot-starter-webflux")) {
      return WebApplicationType.REACTIVE;
    }
    return WebApplicationType.NONE;
  }

  /**
   * read content from file
   * @param file
   * @param encoding
   * @return
   * @throws IOException
   */
  public static String readFileToString(File file, Charset encoding) throws IOException {
    try (FileInputStream stream = new FileInputStream(file)) {
      Reader reader = new BufferedReader(new InputStreamReader(stream, encoding));
      StringBuilder builder = new StringBuilder();
      char[] buffer = new char[8192];
      int read;
      while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
        builder.append(buffer, 0, read);
      }
      return builder.toString();
    }
  }
}
