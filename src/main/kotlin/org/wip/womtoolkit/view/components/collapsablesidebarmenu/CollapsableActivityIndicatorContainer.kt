package org.wip.womtoolkit.view.components.collapsablesidebarmenu

import javafx.beans.property.SimpleIntegerProperty
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.services.activityMonitor.ActivityMonitorService
import org.wip.womtoolkit.model.services.localization.Lsp
import org.wip.womtoolkit.view.components.CircularProgressBar

class CollapsableActivityIndicatorContainer(val container: String) : GridPane() {
	@FXML lateinit var workingIndicator: CircularProgressBar
	@FXML lateinit var title: Label
	@FXML lateinit var stats: Label

	val scope = MainScope()

	init {
		FXMLLoader(javaClass.getResource("/view/components/collapsablesidebarmenu/CollapsableActivityIndicatorContainer.fxml")).apply {
			setRoot(this@CollapsableActivityIndicatorContainer)
			setController(this@CollapsableActivityIndicatorContainer)
			load()
		}
	}



	@FXML
	fun initialize() {
//		title.textProperty().bind(Lsp.lsb("activityIndicator.general.title"))
		scope.launch { ActivityMonitorService[container].queueCount.collect { updateStats() } }
		scope.launch { ActivityMonitorService[container].runningCount.collect { updateStats() } }
		scope.launch { ActivityMonitorService[container].completedCount.collect { updateStats() } }
		scope.launch {
			ActivityMonitorService[container].runningCount.collect {
				workingIndicator.indeterminate = ActivityMonitorService[container].runningCount.value > 0
			}
		}
	}


	private fun updateStats() {
		with(Dispatchers.JavaFx) {
			stats.textProperty().apply {
				unbind()
				bind(
					Lsp.lsb("activityIndicator.stats",
						SimpleIntegerProperty(ActivityMonitorService[container].queueCount.value).asString(),
						SimpleIntegerProperty(ActivityMonitorService[container].runningCount.value).asString(),
						SimpleIntegerProperty(ActivityMonitorService[container].completedCount.value).asString()
					)
				)
			}
		}
	}

}