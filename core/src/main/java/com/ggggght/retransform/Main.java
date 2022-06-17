package com.ggggght.retransform;

import com.ggggght.agent.AgentClassloader;
import com.ggggght.core.CoreBootstrap;
import com.ggggght.jfr.CompilerEvent;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.roots.ModuleRootManager;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.tools.ant.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.WebApplicationType;

public class Main implements ProjectManagerListener {
    static {
        System.load(
            "/Users/wangzheng/github/hotswap/core/src/main/java/com/ggggght/util/libVM.dylib");
        System.out.println("[Agent] libVM.dylib loaded");
    }

    Project project;
    Long pid;
    String projectName;
    WebApplicationType webApplicationType;
    boolean isStart;

    @Override public void projectOpened(@NotNull Project project) {
        ProjectManagerListener.super.projectOpened(project);
        this.project = project;

        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module mod =
            Arrays.stream(modules).filter(m -> m.getName().contains(".main")).findFirst().get();
        VirtualFile[] roots =
            ModuleRootManager.getInstance(mod).orderEntries().classes().getRoots();
        List<String> paths =
            Arrays.stream(roots).map(VirtualFile::getCanonicalPath).collect(Collectors.toList());
        List<String> res = paths.stream()
            .filter(e -> !(e.contains("jdk") || e.contains("java") || e.contains("javafx")))
            .map(s -> s.replace("!/", ""))
            .collect(Collectors.toList());

        String cp = String.join(":", res);
        DynamicCompiler.addCompilerOption(cp);

        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        virtualFileManager.addAsyncFileListener(events -> {
            if (!isStart) return null;

            DynamicCompiler compiler = new DynamicCompiler(CoreBootstrap.classLoader);

            for (VFileEvent event : events) {
                // event.getPath()
                if (event.getFile() == null) continue;
                String sourceName = event.getFile().getName();
                if (!sourceName.contains(".java")) continue;
                System.out.println("current changed fileName is: " + sourceName);
                String fullPath = event.getFile().toNioPath().toString();
                String src = fullPath.substring(fullPath.indexOf("src"));
                src = src.substring(src.indexOf("src/main/java/") + 14)
                    .replace("/", ".")
                    .replace(".java", "");

                try {
                    compiler.addSource(src, FileUtils.readFully(
                        new BufferedReader(new FileReader(
                            event.getFile().getPath()))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // ClassLoader classLoader = this.getClass().getClassLoader();
            // classLoader.getClass()
            ClassLoader classLoader = this.getClass().getClassLoader();
            Map<String, byte[]> byteCodes = compiler.buildByteCodes();

            System.out.println(
                "this.getClass().getClassLoader() = " + classLoader);


            for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
                byte[] value = entry.getValue();
                // Class<?> aClass = agentClassLoader.defineClass(entry.getKey(), value);
                CompilerEvent compilerEvent = new CompilerEvent();
                compilerEvent.message = "HotSwap compile";
                compilerEvent.className = entry.getKey();
                try {
                    // inst.retransformClasses(aClass);
                    compilerEvent.success = true;
                } catch (Exception e) {
                    compilerEvent.success = false;
                    System.out.println(
                        entry.getKey() + " retransform class failed: " + e.getMessage());
                    throw new RuntimeException(e);
                } finally {
                    compilerEvent.commit();
                    System.out.println("event commit success");
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
