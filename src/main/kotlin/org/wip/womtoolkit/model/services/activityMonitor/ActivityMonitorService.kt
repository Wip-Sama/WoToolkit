package org.wip.womtoolkit.model.services.activityMonitor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.locks.ReentrantLock

/**
 * Process(pre) -> Add activity container
 * Process(start) -> add an activity to the container
 * Process(end) -> remove the activity from the container
 *
 * Container -> visibility bound to activity count (> 0)
 * */
object ActivityMonitorService {
	private val containers = mutableMapOf<String, ActivityContainer>()
	private val _nContainers = MutableStateFlow(0)
	val nContainers: StateFlow<Int> = _nContainers
	val lock = ReentrantLock()

	operator fun get(id: String): ActivityContainer {
		return with(lock) {
			val container = containers.getOrPut(id) {
				ActivityContainer()
			}
			_nContainers.value = containers.size
			container
		}
	}
	operator fun set(id: String, container: ActivityContainer) {
	    with(lock) {
	        containers[id] = container
	    }
	}
	fun remove(id: String) {
		with(lock) {
			if (containers.containsKey(id)) {
				containers.remove(id)
				_nContainers.value = containers.size
			}
		}
	}
}