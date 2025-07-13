package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.model.DataManager
import org.wip.womtoolkit.view.pages.LinuxMainWindow
import org.wip.womtoolkit.view.pages.WindowsMainWindow

class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		DataManager.init()

		val osName = System.getProperty("os.name").lowercase()
		println("Operating System: $osName")
		when {
			osName.contains("win") -> {
				println("Initializing Windows Main Window")
				WindowsMainWindow()
			}
			osName.contains("linux") -> {
				println("Initializing Linux Main Window")
				LinuxMainWindow(primaryStage)
			}
			else -> {
				println("Unsupported OS. Exiting.")
				System.exit(1)
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