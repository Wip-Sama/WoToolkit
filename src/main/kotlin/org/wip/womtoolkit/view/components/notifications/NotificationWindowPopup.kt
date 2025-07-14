package org.wip.womtoolkit.view.components.notifications

import javafx.event.EventHandler
import javafx.stage.Popup

class NotificationWindowPopup(

) : Popup() {
	private val notificationWindow = NotificationWindow()
	init {
		isAutoFix = false
		isAutoHide = false
		isHideOnEscape = false
		content.add(notificationWindow)

		notificationWindow.dismiss.onAction = EventHandler {
			hide()
		}
	}
}