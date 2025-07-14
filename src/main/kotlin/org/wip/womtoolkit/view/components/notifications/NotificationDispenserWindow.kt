package org.wip.womtoolkit.view.components.notifications

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

class NotificationDispenserWindow: BorderPane() {
	@FXML lateinit var label: Label
	@FXML lateinit var dismissAll: Button

	init {
		FXMLLoader(javaClass.getResource("/view/components/notification/notificationDispenser.fxml")).apply {
			setRoot(this@NotificationDispenserWindow)
			setController(this@NotificationDispenserWindow)
			load()
		}
	}
}