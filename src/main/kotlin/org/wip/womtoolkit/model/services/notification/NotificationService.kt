package org.wip.womtoolkit.model.services.notification

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.enums.NotificationTypes
import java.util.PriorityQueue

/**
 * Somewhere in the app -> NotificationService.addNotification(notification) -> Add notification to the queue
 * NotificationController (notice changes to the queue) -> display notifications in the UI
 * */
object NotificationService {
	private val _queue: MutableStateFlow<PriorityQueue<NotificationData>> = MutableStateFlow(PriorityQueue { a, b ->
		b.urgency.compareTo(a.urgency)
	})

	val queue: StateFlow<PriorityQueue<NotificationData>>
		get() = _queue.asStateFlow()

	private val _size = MutableStateFlow(0)
	val size = _size.asStateFlow()

	fun addNotification(notification: NotificationData) {
		if (!ApplicationSettings.userSettings.notificationSettings.enabled.value) return
		when(notification.type) {
			NotificationTypes.ERROR -> if (!ApplicationSettings.userSettings.notificationSettings.showError.value) return
			NotificationTypes.WARNING -> if (!ApplicationSettings.userSettings.notificationSettings.showWarning.value) return
			NotificationTypes.INFO -> if (!ApplicationSettings.userSettings.notificationSettings.showInfo.value) return
			NotificationTypes.SUCCESS -> if (!ApplicationSettings.userSettings.notificationSettings.showSuccess.value) return
		}

		val newQueue = PriorityQueue(_queue.value.comparator()).apply {
			addAll(_queue.value)
			add(notification)
		}
		_queue.value = newQueue
		_size.value = newQueue.size
	}

	fun removeNotification(notification: NotificationData) {
		_size.value = (_queue.value.size-1).coerceAtLeast(0)
		val newQueue = PriorityQueue(_queue.value.comparator()).apply {
			addAll(_queue.value.filter { it !== notification })
		}
		_queue.value = newQueue
	}

	fun clearNotifications() {
		_size.value = 0
		_queue.value.clear()
	}
}