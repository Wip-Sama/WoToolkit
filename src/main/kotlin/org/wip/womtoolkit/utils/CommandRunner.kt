package org.wip.womtoolkit.utils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.Globals
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