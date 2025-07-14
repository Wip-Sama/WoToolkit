package org.wip.womtoolkit.model.services.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.PriorityQueue

object NotificationService {
	private val _queue: MutableStateFlow<PriorityQueue<NotificationTemplate>> = MutableStateFlow(PriorityQueue { a, b ->
		a.urgency.compareTo(b.urgency)
	})

	val queue: StateFlow<PriorityQueue<NotificationTemplate>>
		get() = _queue.asStateFlow()

	fun addNotification(notification: NotificationTemplate) {
		_queue.value.add(notification)
	}
}