package org.wip.womtoolkit.view.components.notifications

import javafx.event.EventHandler
import javafx.stage.Popup

class NotificationDispenserPopup(

) : Popup() {
	private val notificationDispenserWindow = NotificationDispenserWindow()
	init {
		isAutoFix = false
		isAutoHide = false
		isHideOnEscape = false
		content.add(notificationDispenserWindow)

		notificationDispenserWindow.dismissAll.onAction = EventHandler {
			hide()
		}
	}
}