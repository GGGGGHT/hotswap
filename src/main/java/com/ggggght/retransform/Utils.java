package com.ggggght.retransform;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {
  public static void retransform() {

  }

  /**
   * 列出当前所有的java进程
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
   * @param projectName 项目名
   * @return pid
   */
  public static Long getCurrentProjectPid(String projectName) {
    return VirtualMachine.list().stream()
        .filter(virtualMachineDescriptor -> virtualMachineDescriptor.displayName()
            .contains(projectName))
        .findFirst()
        .map(virtualMachineDescriptor -> Long.valueOf(virtualMachineDescriptor.id()))
        .orElse(-1L);
  }
}
