package com.ggggght.retransform;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.Topic;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Main implements ProjectManagerListener {

  Project project;

  @Override public void projectOpened(@NotNull Project project) {
    ProjectManagerListener.super.projectOpened(project);
    System.out.println("project.getName() = " + project.getName());
    System.out.println("project.getBasePath() = " + project.getBasePath());
    String projectName = project.getName();
    VirtualFile[] vFiles = ProjectRootManager.getInstance(project)
        .getContentSourceRoots();
    String sourceRootsList = Arrays.stream(vFiles)
        .map(VirtualFile::getUrl)
        .collect(Collectors.joining("\n"));
    Messages.showInfoMessage("Source roots for the " + projectName +
        " plugin:\n" + sourceRootsList, "Project Properties");

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

    project.getMessageBus().connect(project).subscribe(
        Topic.create("run config", RunManagerListener.class),
        new RunManagerListener() {
          @Override public void beforeRunTasksChanged() {
            RunManagerListener.super.beforeRunTasksChanged();
            System.out.println("Main#beforeRunTasksChanged");
          }

          @Override
          public void runConfigurationAdded(@NotNull RunnerAndConfigurationSettings settings) {
            RunManagerListener.super.runConfigurationAdded(settings);
            System.out.println("Main#runConfigurationAdded");
          }

          @Override
          public void runConfigurationRemoved(@NotNull RunnerAndConfigurationSettings settings) {
            RunManagerListener.super.runConfigurationRemoved(settings);
            System.out.println("Main#runConfigurationRemoved");
          }

          @Override
          public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings,
              @Nullable String existingId) {
            RunManagerListener.super.runConfigurationChanged(settings, existingId);
            System.out.println("Main#runConfigurationChanged");
          }

          @Override
          public void runConfigurationChanged(@NotNull RunnerAndConfigurationSettings settings) {
            RunManagerListener.super.runConfigurationChanged(settings);
            System.out.println("Main#runConfigurationChanged");
          }

          @Override public void beginUpdate() {
            RunManagerListener.super.beginUpdate();
            System.out.println("Main#beginUpdate");
          }

          @Override public void endUpdate() {
            RunManagerListener.super.endUpdate();
            System.out.println("Main#endUpdate");
          }

          @Override
          public void stateLoaded(@NotNull RunManager runManager, boolean isFirstLoadState) {
            RunManagerListener.super.stateLoaded(runManager, isFirstLoadState);
            System.out.println("Main#stateLoaded");
          }

          @Override
          public void runConfigurationSelected(@Nullable RunnerAndConfigurationSettings settings) {
            RunManagerListener.super.runConfigurationSelected(settings);
            System.out.println("Main#runConfigurationSelected");
          }
        }
    );

    project.getMessageBus().connect().subscribe(
        XDebuggerManager.TOPIC,
        new XDebuggerManagerListener() {
          @Override public void processStarted(@NotNull XDebugProcess debugProcess) {
            XDebuggerManagerListener.super.processStarted(debugProcess);
            System.out.println("debugger services...");
          }
        }
    );

  }
}
