package com.ggggght.retransform;

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

public class Main implements ProjectManagerListener {

  Project project;

  boolean isWebProject;

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
            System.out.println("debugger services...");
          }
        }
    );


    ModuleManager moduleManager = ModuleManager.getInstance(project);
    Module[] modules = moduleManager.getModules();
    isWebProject = Utils.isWebModule(modules[0]);
  }


}
