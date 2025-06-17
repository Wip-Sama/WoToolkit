package org.wip.womtoolkit.components.collapsablesidebarmenu

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.shape.SVGPath
import org.wip.womtoolkit.model.LocalizationService

class CollapsableComponent() : GridPane(), CollapsableItem {
	@FXML private lateinit var text: Label
	@FXML private lateinit var svg: SVGPath

	override var localizaionKey: String? = null
	override var selectable: Boolean = true

	override val onActionProperty: BooleanProperty = SimpleBooleanProperty(false)
	val label get() = text
	val icon get() = svg

	init {
		FXMLLoader(javaClass.getResource("/components/collapsablesidebarmenu/CollapsableComponent.fxml")).apply {
			setRoot(this@CollapsableComponent)
			setController(this@CollapsableComponent)
			load()
		}
	}

	override fun expand() {
		if (localizaionKey == null) {
			return
		}
		text.textProperty().bind(LocalizationService.lsb(localizaionKey))
	}

	override fun collapse() {
		text.textProperty().unbind()
		text.text = ""
	}

	override fun select() {
		pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true)
	}

	override fun deselect() {
		pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false)
	}

	@FXML
	fun onAction() {
		onActionProperty.value = !onActionProperty.value
	}
}