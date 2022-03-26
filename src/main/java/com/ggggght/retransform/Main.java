package com.ggggght.retransform;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Main implements ProjectManagerListener{

  @Override public void projectOpened(@NotNull Project project) {
    ProjectManagerListener.super.projectOpened(project);
    System.out.println("project.getName() = " + project.getName());
    System.out.println("project.getBasePath() = " + project.getBasePath());
    project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES,
        new BulkFileListener() {
          @Override
          public void after(@NotNull List<? extends VFileEvent> events) {
            // handle the events
            for (VFileEvent event : events) {
              VirtualFile file = event.getFile();
              if (Objects.isNull(file)) continue;
              var fileName = file.getName();
              if (fileName.isBlank() || !fileName.contains(".java")) continue;
              // todo retransform this class
            }
          }
        });
  }
}
