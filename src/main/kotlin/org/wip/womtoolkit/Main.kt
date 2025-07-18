package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.model.DataManager
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.view.pages.LinuxMainWindow
import org.wip.womtoolkit.view.pages.WindowsMainWindow
import kotlin.system.exitProcess

class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		DataManager.init()

		val osName = System.getProperty("os.name").lowercase()
		Globals.logger.info("Operating System: $osName")
		when {
			osName.contains("win") -> {
				Globals.logger.info("Initializing Windows Main Window")
				WindowsMainWindow()
			}
			osName.contains("linux") -> {
				Globals.logger.info("Initializing Linux Main Window")
				LinuxMainWindow(primaryStage)
			}
			else -> {
				Globals.logger.warning("Unsupported OS. Exiting.")
				exitProcess(1)
			}
		}
	}
	override fun stop() {
		DataManager.close()
	}
}

fun main() {
	try {
		Application.launch(WomToolkit::class.java)
	} catch (e: Exception) {
		e.printStackTrace()
	}
}