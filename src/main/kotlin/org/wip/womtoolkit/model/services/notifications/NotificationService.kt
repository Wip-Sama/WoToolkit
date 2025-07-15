package org.wip.womtoolkit.model.services.notifications

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

	val sizeProperty: IntegerProperty = SimpleIntegerProperty(0)

	fun addNotification(notification: NotificationData) {
		val newQueue = PriorityQueue(_queue.value.comparator()).apply {
			addAll(_queue.value)
			add(notification)
		}
		_queue.value = newQueue
		sizeProperty.value = newQueue.size
	}

	fun removeNotification(notification: NotificationData) {
		val newQueue = PriorityQueue(_queue.value.comparator()).apply {
			addAll(_queue.value.filter { it !== notification })
		}
		_queue.value = newQueue
		sizeProperty.value = newQueue.size
	}
}