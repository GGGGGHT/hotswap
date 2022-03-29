package com.ggggght.retransform;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.WebApplicationType;

public class Main implements ProjectManagerListener {

  Project project;
  Long pid;
  String projectName;
  WebApplicationType webApplicationType;

  @Override public void projectOpened(@NotNull Project project) {
    ProjectManagerListener.super.projectOpened(project);
    this.project = project;

    VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
    virtualFileManager.addAsyncFileListener(events -> {
      for (VFileEvent event : events) {
        if(event.getFile() == null) continue;
        if (!event.getFile().getName().contains(".java")) continue;
        System.out.println("current changed fileName is: " + event.getFile().getName());
      }
      return new AsyncFileListener.ChangeApplier() {
        @Override public void beforeVfsChange() {
          AsyncFileListener.ChangeApplier.super.beforeVfsChange();
        }

        @Override public void afterVfsChange() {
          AsyncFileListener.ChangeApplier.super.afterVfsChange();
        }
      };
    }, () -> {});

    // 监听debugger事件
    project.getMessageBus().connect().subscribe(
        XDebuggerManager.TOPIC,
        new XDebuggerManagerListener() {
          @Override public void processStarted(@NotNull XDebugProcess debugProcess) {
            XDebuggerManagerListener.super.processStarted(debugProcess);
            pid = Utils.getCurrentProjectPid(projectName);
          }
        }
    );

    // 监听服务启动
    // https://intellij-support.jetbrains.com/hc/en-us/community/posts/4833708195474-How-do-I-listen-to-run-events-within-my-own-intellij-plugin-
    project.getMessageBus().connect().subscribe(
        ExecutionManager.EXECUTION_TOPIC,
        new ExecutionListener() {
          @Override
          public void processStarting(@NotNull String executorId, @NotNull ExecutionEnvironment env,
              @NotNull ProcessHandler handler) {
            ExecutionListener.super.processStarting(executorId, env, handler);
            pid = Utils.getCurrentProjectPid(projectName);
            System.out.println("服务启动了...");
          }
        }
    );


    ModuleManager moduleManager = ModuleManager.getInstance(project);
    Module[] modules = moduleManager.getModules();
    webApplicationType = Utils.isWebModule(modules[0]);
  }


}
