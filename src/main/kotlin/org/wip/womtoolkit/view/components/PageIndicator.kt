package org.wip.womtoolkit.view.components

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.shape.SVGPath

/**
 * Should be able to add/remove label and add between them a svg ">"
 */
class PageIndicator : HBox() {
	object Contants {
		const val ARROW: String =
			"M8.29289 4.29289C7.90237 4.68342 7.90237 5.31658 8.29289 5.70711L14.5858 12L8.29289 18.2929C7.90237 18.6834 7.90237 19.3166 8.29289 19.7071C8.68342 20.0976 9.31658 20.0976 9.70711 19.7071L16.7071 12.7071C17.0976 12.3166 17.0976 11.6834 16.7071 11.2929L9.70711 4.29289C9.31658 3.90237 8.68342 3.90237 8.29289 4.29289Z"
	}

	init {
		FXMLLoader(javaClass.getResource("/components/pageIndicator.fxml")).apply {
			setRoot(this@PageIndicator)
			setController(this@PageIndicator)
			load()
		}
	}

	@FXML
	fun initialize() {
	}

	var labels: List<Label> = mutableListOf()
		private set

	fun updateRoot() {
		children.clear()
		if (labels.isEmpty())
			return
		for (label in labels) {
			children.add(label)
			children.add(SVGPath().apply {
				content = Contants.ARROW
				scaleX = .8
				scaleY = .8
			})
		}
		children.removeAt(children.size - 1)
	}

	fun addLabel(label: Label) {
		labels += label
		updateRoot()
	}

	fun removeLabel() {
		labels = labels.dropLast(1)
		updateRoot()
	}
}