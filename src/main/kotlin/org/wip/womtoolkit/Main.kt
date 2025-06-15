package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.view.MainWindow


class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		MainWindow().start()
	}
}

fun main() {
	try {
		println("Main avviato")
		Application.launch(WomToolkit::class.java)
	} catch (e: Exception) {
		e.printStackTrace()
	}
}