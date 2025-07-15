package org.wip.womtoolkit.model.services.notifications

import org.wip.womtoolkit.model.enums.NotificationTypes

data class NotificationData(
	var localizedContent: String,
	var type: NotificationTypes,
	var autoStart: Boolean = false,
	var autoDismiss: Boolean = true,
	var autoDismissDelay: Double = 5000.0,
	var urgency: Int = 0, // only used for the position in the queue (higher is shown first)
)
