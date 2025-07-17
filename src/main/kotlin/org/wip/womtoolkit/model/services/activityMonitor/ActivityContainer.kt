package org.wip.womtoolkit.model.services.activityMonitor

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.locks.ReentrantLock

/**
 * */
class ActivityContainer {
	val threads = mutableListOf<Thread>()
	val lock = ReentrantLock()

	val queueCount = MutableStateFlow(0)
	val runningCount = MutableStateFlow(0)
	val completedCount = MutableStateFlow(0)
}