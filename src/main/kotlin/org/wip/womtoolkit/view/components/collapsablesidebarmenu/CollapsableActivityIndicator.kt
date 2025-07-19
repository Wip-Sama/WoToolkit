package org.wip.womtoolkit.view.components.collapsablesidebarmenu

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.css.PseudoClass
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.services.activityMonitor.ActivityMonitorService
import org.wip.womtoolkit.model.services.localization.Lsp
import org.wip.womtoolkit.view.components.CircularProgressBar

class CollapsableActivityIndicator : AnchorPane(), CollapsableItem {
	@FXML lateinit var workingIndicator: CircularProgressBar
	@FXML lateinit var generalTitle: Label
	@FXML lateinit var generalStats: Label
	@FXML lateinit var scrollContainer: ScrollPane
	@FXML lateinit var generalDisplay: GridPane
	@FXML lateinit var containersVbox: VBox

	override var localizationKey: String? = null
	override var selectable: Boolean = false
	override val onActionProperty: BooleanProperty = SimpleBooleanProperty(false)

	val scope = MainScope()

	val rectClip = Rectangle().apply {
//		arcHeight = 13.0
//		arcWidth = 13.0
	}

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
			val nH = if (newValue) 36.0*3*1 else 36.0
			Timeline(
				KeyFrame(Duration.millis(100.0), KeyValue(prefHeightProperty(), nH))
			).apply {
				play()
			}
		}
		generalTitle.textProperty().bind(Lsp.lsb("activityIndicator.general.title"))
		scope.launch { ActivityMonitorService.queueCount.collect { updateGeneralStats() } }
		scope.launch { ActivityMonitorService.runningCount.collect { updateGeneralStats() } }
		scope.launch { ActivityMonitorService.completedCount.collect { updateGeneralStats() } }
		scope.launch {
			ActivityMonitorService.runningCount.collect {
				workingIndicator.indeterminate = ActivityMonitorService.runningCount.value > 0
			}
		}
		scope.launch {
			ActivityMonitorService.nContainers.collect { value ->
				if ( value != containersVbox.children.size ) {
					containersVbox.children.clear()
					ActivityMonitorService.lock.lock()
					try {
						ActivityMonitorService.containers.forEach { container ->
							containersVbox.children.add(CollapsableActivityIndicatorContainer(container.key))
						}
					} finally {
						ActivityMonitorService.lock.unlock()
					}
				}
			}
		}
		scrollContainer.clip = rectClip
		rectClip.apply {
			widthProperty().bind(scrollContainer.widthProperty())
			minHeight(0.0)
			heightProperty().bind(this@CollapsableActivityIndicator.heightProperty().subtract(generalDisplay.heightProperty()))
		}
	}

	override fun select() {}
	override fun deselect() {}
	override fun expand() {}
	override fun collapse() {}

	private fun updateGeneralStats() {
		with(Dispatchers.JavaFx) {
			generalStats.textProperty().apply {
				unbind()
				bind(
					Lsp.lsb("activityIndicator.stats",
						SimpleIntegerProperty(ActivityMonitorService.queueCount.value).asString(),
						SimpleIntegerProperty(ActivityMonitorService.runningCount.value).asString(),
						SimpleIntegerProperty(ActivityMonitorService.completedCount.value).asString()
					)
				)
			}
		}
	}

	@FXML
	fun onAction() {
		onActionProperty.value = !onActionProperty.value

		pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), onActionProperty.value)
	}
}