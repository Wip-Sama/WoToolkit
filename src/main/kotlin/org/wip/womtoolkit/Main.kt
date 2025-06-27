package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.model.DataManager
import org.wip.womtoolkit.view.pages.MainWindow

class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		DataManager.init()
		MainWindow()
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