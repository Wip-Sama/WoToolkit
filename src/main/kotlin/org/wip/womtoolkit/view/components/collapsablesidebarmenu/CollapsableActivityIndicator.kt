package org.wip.womtoolkit.view.components.collapsablesidebarmenu

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.BorderPane
import javafx.util.Duration

object CollapsableActivityIndicator : BorderPane(), CollapsableItem {
	override var localizationKey: String? = null
	override var selectable: Boolean = false
	override val onActionProperty: BooleanProperty = SimpleBooleanProperty(false)

	init {
		FXMLLoader(javaClass.getResource("/view/components/collapsablesidebarmenu/CollapsableActivityIndicator.fxml")).apply {
			setRoot(this@CollapsableActivityIndicator)
			setController(this@CollapsableActivityIndicator)
			load()
		}
	}

	@FXML
	fun initialize() {
		onActionProperty.addListener { _, _, newValue ->
			val nH = if (newValue) 32.0 else 128.0
			Timeline(
				KeyFrame(Duration.millis(100.0), KeyValue(prefHeightProperty(), nH))
			).apply {
				play()
			}
		}
	}

	override fun select() {}
	override fun deselect() {}
	override fun expand() {

	}
	override fun collapse() {

	}

	@FXML
	fun onAction() {
		onActionProperty.value = !onActionProperty.value
	}
}