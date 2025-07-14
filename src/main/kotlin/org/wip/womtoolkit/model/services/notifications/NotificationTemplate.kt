package org.wip.womtoolkit.model.services.notifications

import org.wip.womtoolkit.model.enums.NotificationTypes

data class NotificationTemplate(
	var localizedContent: String,
	var type: NotificationTypes,
	var urgency: Int = 0,
)
