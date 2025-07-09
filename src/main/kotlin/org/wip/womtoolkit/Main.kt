package org.wip.womtoolkit

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TitledPane
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.DataManager
import org.wip.womtoolkit.utils.serializers.ColorSerializer
import org.wip.womtoolkit.view.pages.MainWindow
import java.util.*

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