package com.ggggght.retransform;

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
    // TODO replace magic with config

    /**
     * 添加jvm启动参数
     *
     * @param configuration
     * @param params
     * @param runnerSettings
     * @param <T>
     */
    @Override
    public <T extends RunConfigurationBase<?>> void updateJavaParameters(@NotNull T configuration,
        @NotNull JavaParameters params, RunnerSettings runnerSettings) {
        Path path = Objects.requireNonNull(
                PluginManagerCore.getPlugin(PluginId.getId("com.github.ggggght.hotswap")))
            .getPluginPath();
        String separator = File.separator;
        String agentPath = path.toString() + separator + "lib" + separator + "agent-0.0.1.jar";
        params.getVMParametersList().add("-javaagent:" + agentPath);
    }

    @Override public boolean isApplicableFor(@NotNull RunConfigurationBase<?> configuration) {
        return true;
    }
}
