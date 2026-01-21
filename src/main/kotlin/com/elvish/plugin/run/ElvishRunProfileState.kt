package com.elvish.plugin.run

import com.elvish.plugin.settings.ElvishSettings
import com.intellij.execution.DefaultExecutionResult
import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Run profile state for executing Elvish scripts.
 * Handles process creation and output display in the Run tool window.
 */
class ElvishRunProfileState(
    environment: ExecutionEnvironment,
    private val configuration: ElvishRunConfiguration
) : CommandLineState(environment) {

    override fun startProcess(): ProcessHandler {
        val commandLine = createCommandLine()
        LOG.info("Executing Elvish script: ${commandLine.commandLineString}")

        val processHandler = KillableColoredProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    override fun execute(executor: Executor, runner: ProgramRunner<*>): ExecutionResult {
        val processHandler = startProcess()
        val console = createConsole(executor)
        console?.attachToProcess(processHandler)

        return DefaultExecutionResult(console, processHandler, *createActions(console, processHandler, executor))
    }

    /**
     * Creates the command line for executing the Elvish script.
     */
    private fun createCommandLine(): GeneralCommandLine {
        val elvishPath = getElvishPath()
        val scriptPath = configuration.scriptPath
        val arguments = configuration.scriptArguments

        val commandLine = GeneralCommandLine()
            .withExePath(elvishPath)
            .withCharset(StandardCharsets.UTF_8)

        // Add the script path as the first argument
        commandLine.addParameter(scriptPath)

        // Add script arguments if present
        if (arguments.isNotBlank()) {
            // Split arguments respecting quoted strings
            val args = parseArguments(arguments)
            args.forEach { commandLine.addParameter(it) }
        }

        // Set working directory
        val workingDir = configuration.workingDirectory.takeIf { it.isNotBlank() }
            ?: configuration.project.basePath
        if (workingDir != null) {
            commandLine.setWorkDirectory(File(workingDir))
        }

        // Set environment variables
        if (configuration.passParentEnvs) {
            commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
        } else {
            commandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.NONE)
        }

        configuration.environmentVariables.forEach { (key, value) ->
            commandLine.withEnvironment(key, value)
        }

        return commandLine
    }

    /**
     * Gets the path to the Elvish binary, either from run configuration or project settings.
     */
    private fun getElvishPath(): String {
        return if (configuration.useElvishFromSettings) {
            val settings = ElvishSettings.getInstance(configuration.project)
            settings.elvishPath.takeIf { it.isNotBlank() } ?: DEFAULT_ELVISH_PATH
        } else {
            configuration.customElvishPath.takeIf { it.isNotBlank() } ?: DEFAULT_ELVISH_PATH
        }
    }

    /**
     * Parses command line arguments, respecting quoted strings.
     */
    private fun parseArguments(arguments: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inSingleQuote = false
        var inDoubleQuote = false
        var escaped = false

        for (char in arguments) {
            when {
                escaped -> {
                    current.append(char)
                    escaped = false
                }
                char == '\\' && !inSingleQuote -> {
                    escaped = true
                }
                char == '\'' && !inDoubleQuote -> {
                    inSingleQuote = !inSingleQuote
                }
                char == '"' && !inSingleQuote -> {
                    inDoubleQuote = !inDoubleQuote
                }
                char.isWhitespace() && !inSingleQuote && !inDoubleQuote -> {
                    if (current.isNotEmpty()) {
                        result.add(current.toString())
                        current.clear()
                    }
                }
                else -> {
                    current.append(char)
                }
            }
        }

        if (current.isNotEmpty()) {
            result.add(current.toString())
        }

        return result
    }

    companion object {
        private val LOG = Logger.getInstance(ElvishRunProfileState::class.java)
        private const val DEFAULT_ELVISH_PATH = "elvish"
    }
}
