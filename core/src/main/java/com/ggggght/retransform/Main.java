package com.ggggght.retransform;

import com.ggggght.agent.AgentClassloader;
import com.ggggght.core.CoreBootstrap;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.apache.tools.ant.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.WebApplicationType;

public class Main implements ProjectManagerListener {

    Project project;
    Long pid;
    String projectName;
    WebApplicationType webApplicationType;
    static ClassLoader classLoader;
    boolean isStart;
    @Override public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);
        this.project = project;

        Module[] modules = ModuleManager.getInstance(project).getModules();
        VirtualFile[] roots = ModuleRootManager.getInstance(modules[0]).orderEntries().classes().getRoots();
        List<String> classPath =
            Arrays.stream(roots).map(VirtualFile::getPath).collect(Collectors.toList());
        System.out.println("classPath = " + classPath);

        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        virtualFileManager.addAsyncFileListener(events -> {
           if(!isStart) return null;

            DynamicCompiler compiler = new DynamicCompiler(classLoader);
            for (VFileEvent event : events) {
                // event.getPath()
                if (event.getFile() == null) continue;
                String sourceName = event.getFile().getName();
                if (!sourceName.contains(".java")) continue;
                System.out.println("current changed fileName is: " + sourceName);

                try {
                    compiler.addSource(sourceName, FileUtils.readFully(
                        new BufferedReader(new FileReader(
                            event.getFile().getPath()))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Map<String, byte[]> byteCodes = compiler.buildByteCodes();
            Instrumentation inst = CoreBootstrap.getInstrumentation();
            for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
                byte[] value = entry.getValue();
                AgentClassloader agentClassLoader = (AgentClassloader) classLoader;
                Class<?> aClass = agentClassLoader.defineClass(entry.getKey(), value);
                // agentClassLoader
                try {
                    inst.retransformClasses(aClass);
                } catch (UnmodifiableClassException e) {
                    System.out.println(entry.getKey() + " retransform class failed: " + e.getMessage());
                    throw new RuntimeException(e);
                }

                System.out.println(entry.getKey() + " is retransformed");
            }

            return new AsyncFileListener.ChangeApplier() {
                @Override public void beforeVfsChange() {
                    AsyncFileListener.ChangeApplier.super.beforeVfsChange();
                }

                @Override public void afterVfsChange() {
                    AsyncFileListener.ChangeApplier.super.afterVfsChange();
                }
            };
        }, () -> {
        });

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
                public void processStarting(@NotNull String executorId,
                    @NotNull ExecutionEnvironment env,
                    @NotNull ProcessHandler handler) {
                    ExecutionListener.super.processStarting(executorId, env, handler);
                    // pid = Utils.getCurrentProjectPid(projectName);
                    System.out.println("服务启动了...");
                    isStart = true;
                }
            }
        );

        // ModuleManager moduleManager = ModuleManager.getInstance(project);
        // Module[] modules = moduleManager.getModules();
        // webApplicationType = Utils.isWebModule(modules[0]);
    }
}
