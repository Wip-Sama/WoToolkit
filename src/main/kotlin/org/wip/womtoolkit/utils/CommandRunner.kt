package org.wip.womtoolkit.utils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.enums.Platforms
import org.wip.womtoolkit.model.services.modulesManagment.ModuleManagementService
import java.io.File
import java.io.InputStream

class CommandRunner {
	companion object {
		fun cleanAnsiCodes(input: String): String {
			val ansiRegex = Regex("\\u001B\\[[0-9;?]*[A-Za-z]")
			return ansiRegex.replace(input, "").replace("\r", "").replace("\n", "").trim()
		}

		/**
		 *  Return the list of commands needed to run a command inside PowerShell
		 *  */
		fun runInPowershell(bypassExecutionPolicy: Boolean = false): MutableList<String> {
			return mutableListOf("powershell.exe", "-Command").apply {
				if (bypassExecutionPolicy) add("Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process -Force") // Allow script execution
			}
		}

		/**
		 *  Return the list of commands needed to run a command inside Bash
		 *  */
		fun runInBash(): MutableList<String> {
			return mutableListOf("bash", "-c")
		}

		/**
		 *  Return the list of commands needed to run a command inside CMD
		 *  */
		fun runInCmd(): MutableList<String> {
			return mutableListOf("cmd.exe", "/C")
		}

		/**
		 *  Return the list of commands needed to run a command inside the default shell for the given platform
		 *  @throws RuntimeException if the platform is unsupported
		 *  */
		fun getRunnerForPlatform(platform: Platforms = Globals.PLATFORM): MutableList<String> {
			return when (platform) {
				Platforms.LINUX, Platforms.MACOS -> {
					runInBash()
				}
				Platforms.WINDOWS -> {
					runInPowershell()
				}
				else -> {
					Globals.logger.warning("Unsupported platform: ${platform.name}")
					throw RuntimeException("Unsupported platform: ${platform.name}")
				}
			}
		}

		fun runUsingPythonVenv(venvPath: String): MutableList<String> {
			return mutableListOf(
				"$venvPath/bin/python",
				"-c"
			)
		}

		fun customProcessBuilder(command: List<String>? = null): ProcessBuilder {
			return ProcessBuilder(command ?: listOf())
				.directory(File(System.getProperty("user.dir")))
		}

		fun setBruteForceReaderForStream(stream: InputStream, onOutput: (String) -> Unit, cleanAnsiCodes: Boolean = true) {
			stream.bufferedReader().use { reader ->
				var line: String?
				do {
					line = reader.readLine()
					if (line != null) {
						onOutput(if (cleanAnsiCodes) cleanAnsiCodes(line) else line)
					} else {
						Thread.sleep(500)
					}
				} while (line != null)
			}
		}

		fun setReaderForStream(stream: InputStream, onOutput: (String) -> Unit, cleanAnsiCodes: Boolean = true) {
			stream.bufferedReader().use { reader ->
				reader.lines().forEach { line ->
					onOutput(if (cleanAnsiCodes) cleanAnsiCodes(line) else line)
				}
			}
		}

		/**
		 * Runs a simple command and returns the standard output and error as a pair of strings.
		 * @param command The command to run.
		 * @return A pair containing the standard output and standard error.
		 */
		fun runSimpleCommand(command: String): Pair<String, String> {
			val stdOut: StringBuilder = StringBuilder()
			val stdErr: StringBuilder = StringBuilder()

			customProcessBuilder(getRunnerForPlatform().apply {
				add(command)
			}).start().apply {
				setReaderForStream(inputStream, { line -> stdOut.append(line) }, cleanAnsiCodes = true)
				setReaderForStream(errorStream, { line -> stdErr.append(line) }, cleanAnsiCodes = true)
				waitFor()
			}

			return Pair(stdOut.toString(), stdErr.toString())
		}

		/**
		 * This function replaces dependencies in the command string with their actual interfaces.
		 * The command string can contain dependencies in the format {dependencyName.interfaceName}.
		 * @throws RuntimeException if the dependency syntax is unsupported or if the dependency is not found.
		 */
		fun replaceDependenciesInCommand(command: String): String {
			val dependencies = Regex("\\{([^}]*)}").findAll(command)
			var newCommand = command

			dependencies.forEach { matchResult ->
				val parts = matchResult.value.removePrefix("{").removeSuffix("}").split('.')
				if (parts.size != 2) {
					Globals.logger.warning("Unsupported dependency syntax $command")
					throw RuntimeException("Unsupported dependency syntax $command")
				}
				val dependencyName = parts[0]
				val dependencyInterfaceName = parts[1]

				val dependencyModule = ModuleManagementService.modules[dependencyName]?: return@forEach
				val dependencyModulePlatform = dependencyModule.supportedPlatforms.value[Globals.PLATFORM.name.lowercase()]?: return@forEach
				val dependencyModuleInterface = dependencyModulePlatform.interfaces.value[dependencyInterfaceName]?: return@forEach

				newCommand = newCommand.replace(matchResult.value, dependencyModuleInterface.getInterface())
			}

			return newCommand
		}
	}

	val scope = MainScope()

	fun runCommand(command: String, onOutput: (String) -> Unit, onError: (String) -> Unit) {
		scope.launch {
			try {
				val process = ProcessBuilder(command.split(" "))
					.directory(File(System.getProperty("user.dir")))
					.start()

				process.inputStream.bufferedReader().use { reader ->
					reader.lines().forEach { line ->
						onOutput(cleanAnsiCodes(line))
					}
				}

				process.errorStream.bufferedReader().use { reader ->
					reader.lines().forEach { line ->
						onError(cleanAnsiCodes(line))
					}
				}

				val exitCode = process.waitFor()
				if (exitCode != 0) {
					Globals.logger.severe { "Command execution failed: $command" }
					onError("Process exited with code $exitCode")
				}
			} catch (e: Exception) {
				Globals.logger.severe { "Error running command: ${e.message}" }
				onError("Error running command: ${e.message}")
			}
		}
	}
}