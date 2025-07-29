package org.wip.womtoolkit.utils

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.Globals
import java.io.File

class CommandRunner {

	companion object {
		fun cleanAnsiCodes(input: String): String {
			val ansiRegex = Regex("\\u001B\\[[0-9;?]*[A-Za-z]")
			return ansiRegex.replace(input, "").replace("\r", "").replace("\n", "").trim()
		}

		fun runInPowershell(): MutableList<String> {
			return mutableListOf("powershell.exe", "-Command")
		}
	}

	val scope = MainScope()


	fun runCommand(command: String, onOutput: (String) -> Unit, onError: (String) -> Unit) {
		scope.launch {
			try {
				val process = ProcessBuilder(command.split(" "))
					.directory(File(System.getProperty("user.dir")))
					.start()

//              Brute force reader
//				do {
//					line = reader.readLine()
//					if (line != null) {
//      				onOutput(cleanAnsiCodes(line))
//					} else {
//						delay(500)
//					}
//				} while (process.isAlive || line != null)

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