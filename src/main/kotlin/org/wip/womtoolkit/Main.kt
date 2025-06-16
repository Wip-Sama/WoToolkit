package org.wip.womtoolkit

import javafx.application.Application
import javafx.stage.Stage
import org.wip.womtoolkit.model.LocalizationService
import org.wip.womtoolkit.view.MainWindow


class WomToolkit : Application() {
	override fun start(primaryStage: Stage) {
		LocalizationService.currentLocale = "itIT"
		MainWindow().start()
	}
}

fun main() {
	try {
		Application.launch(WomToolkit::class.java)
	} catch (e: Exception) {
		e.printStackTrace()
	}
}