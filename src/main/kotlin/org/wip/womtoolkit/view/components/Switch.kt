package org.wip.womtoolkit.view.components

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.css.PseudoClass
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import org.wip.womtoolkit.model.Lsp
import kotlin.properties.Delegates

class Switch() : BorderPane() {
	@FXML private lateinit var switchStateHolder: AnchorPane
	@FXML private lateinit var switchStateIndicator: Pane
	@FXML lateinit var textStateIndicator: Label

	var falseLocalization: String? by Delegates.observable(null) { _, _, newValue ->
		updateLocalization()
	}
	var trueLocalization: String? by Delegates.observable(null) { _, _, newValue ->
		updateLocalization()
	}

	var stateProperty: BooleanProperty = SimpleBooleanProperty(false) // always left
		private set

	var state: Boolean
		get() = stateProperty.value
		set(value) { stateProperty.value = value }

	init {
		FXMLLoader(javaClass.getResource("/components/switch.fxml")).apply {
			setRoot(this@Switch)
			setController(this@Switch)
			load()
		}
	}

	constructor(state: Boolean) : this() {
		stateProperty.value = state
	}

	@FXML
	fun initialize() {
		stateProperty.addListener(ChangeListener { _, _, new: Boolean? ->
			animateTransition()
			updateLocalization()
		})

		pressedProperty().addListener { observable, oldValue, newValue ->
			if (newValue == true) {
				Timeline(
					KeyFrame(Duration.millis(100.0),
						KeyValue(switchStateIndicator.prefWidthProperty(), switchStateIndicator.maxWidth)
					)
				).play()
			} else {
				Timeline(
					KeyFrame(Duration.millis(100.0),
						KeyValue(switchStateIndicator.prefWidthProperty(), switchStateIndicator.minWidth)
					)
				).play()
			}
		}

		onMouseClicked = EventHandler { event: MouseEvent? ->
			state = !state
		}
		updateLocalization()
	}

	private fun animateTransition(value: Boolean = stateProperty.value) {
		val endPosition = if (value == true) switchStateHolder.width-switchStateIndicator.width else 0
		Timeline(
			KeyFrame(Duration.ZERO, { AnchorPane.clearConstraints(switchStateIndicator) } ),
			KeyFrame(
				Duration.millis(100.0),
				KeyValue(switchStateIndicator.layoutXProperty(), endPosition),
			),
			KeyFrame(Duration.millis(100.0), {
				if (value == true) {
					AnchorPane.setRightAnchor(switchStateIndicator, 1.0)
					pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true)
				} else {
					AnchorPane.setLeftAnchor(switchStateIndicator, 1.0)
					pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false)
				}
			} ),
		).play()
	}

	private fun updateLocalization(value: Boolean = stateProperty.value) {
		if (value) {
			if (trueLocalization != null) {
				textStateIndicator.textProperty().unbind()
				textStateIndicator.textProperty().bind(Lsp.lsb(trueLocalization))
			}
		} else {
			if (falseLocalization != null) {
				textStateIndicator.textProperty().unbind()
				textStateIndicator.textProperty().bind(Lsp.lsb(falseLocalization))
			}
		}
	}

}