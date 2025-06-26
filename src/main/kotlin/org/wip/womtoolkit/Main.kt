package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.model.InitializeModel
import org.wip.womtoolkit.view.MainWindow

class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		InitializeModel.init()
		MainWindow()
	}
	override fun stop() {
		InitializeModel.close()
	}
}

fun main() {
	try {
		Application.launch(WomToolkit::class.java)
	} catch (e: Exception) {
		e.printStackTrace()
	}
}