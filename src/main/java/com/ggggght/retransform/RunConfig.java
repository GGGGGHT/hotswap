package com.ggggght.retransform;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import java.io.File;
import org.jetbrains.annotations.NotNull;

public class RunConfig extends RunConfigurationExtension {
  @Override
  public <T extends RunConfigurationBase<?>> void updateJavaParameters(@NotNull T configuration,
      @NotNull JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
    // TODO: 是否支持从插件本地加载jar包
    params.getVMParametersList().add(
        "-javaagent:/lib/agent-1.0-SNAPSHOT.jar" );
  }

  @Override public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
    return true;
  }
}
