package com.ggggght.retransform;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class RunConfig extends RunConfigurationExtension {
  @Override
  public <T extends RunConfigurationBase<?>> void updateJavaParameters(@NotNull T configuration,
      @NotNull JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
    Path path = Objects.requireNonNull(
        PluginManagerCore.getPlugin(PluginId.getId("com.github.ggggght.hotswap"))).getPluginPath();
    String separator = File.separator;
    String agentPath = path.toString() + separator + "lib" + separator + "agent-1.0-SNAPSHOT.jar";
    params.getVMParametersList().add("-javaagent:" + agentPath);

  }

  @Override public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
    return true;
  }
}
