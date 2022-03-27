package com.ggggght.retransform;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public final class Utils {
  public static void retransform() {

  }

  /**
   * 列出当前所有的java进程
   *
   * @return 当前机器所有的java进程
   */
  public static List<Long> getAllJavaProcessId() {
    return VirtualMachine.list()
        .stream()
        .map(VirtualMachineDescriptor::id)
        .map(Long::valueOf)
        .collect(Collectors.toList());
  }

  /**
   * 找到当前项目的pid 如果为-1 则意为当前项目未启动
   *
   * @param projectName 项目名
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
   * 获取当前模块所使用的jdk版本
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
   * 获取当前模块所有的依赖
   *
   * @param module 当前模块
   * @return List<String>
   */
  public static List<String> getAllDependencies(@NotNull Module module) {
    List<String> libraries = new ArrayList<>();
    ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(library -> {
      String[] split = Objects.requireNonNull(library.getName()).split(":");
      libraries.add(split[2]);
      return true;
    });

    return libraries;
  }

  /**
   * 判断当前项目是否是web项目
   * @param module 当前模块
   * @return isWebModule
   */
  public static boolean isWebModule(@NotNull Module module) {
    List<String> dependencies = getAllDependencies(module);
    return dependencies.contains("spring-boot-starter-web") || dependencies.contains(
        "spring-boot-starter-webflux");
  }
}
