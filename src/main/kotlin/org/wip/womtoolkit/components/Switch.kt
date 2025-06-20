package org.wip.womtoolkit.components

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

class Switch() : BorderPane() {
	@FXML private lateinit var switchStateHolder: AnchorPane
	@FXML private lateinit var switchStateIndicator: Pane
	@FXML private lateinit var textStateIndicator: Label

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
		stateProperty.addListener(ChangeListener { _, old, new: Boolean? ->
			val endPosition = if (new == true) switchStateHolder.width-switchStateIndicator.width else 0
			Timeline(
				KeyFrame(Duration.ZERO, { AnchorPane.clearConstraints(switchStateIndicator) } ),
				KeyFrame(
					Duration.millis(100.0),
					KeyValue(switchStateIndicator.layoutXProperty(), endPosition),
				),
				KeyFrame(Duration.millis(100.0), {
					if (new == true) {
						AnchorPane.setRightAnchor(switchStateIndicator, 1.0)
						pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true)
					} else {
						AnchorPane.setLeftAnchor(switchStateIndicator, 1.0)
						pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false)
					}
				} ),
			).play()
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
	}
}