package org.wip.womtoolkit.model.services.notification

import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.enums.NotificationTypes

data class NotificationData(
	var localizedContent: String,
	var type: NotificationTypes,
	var autoDismiss: Boolean = ApplicationSettings.userSettings.notificationSettings.autoDismiss.value,
	var autoDismissDelay: Double = (ApplicationSettings.userSettings.notificationSettings.autoDismissTime.value*1000).toDouble(),
	var urgency: Int = 0, // only used for the position in the queue (higher is shown first)
)
