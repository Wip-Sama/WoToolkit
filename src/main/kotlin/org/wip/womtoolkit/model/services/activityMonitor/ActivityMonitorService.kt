package org.wip.womtoolkit.model.services.activityMonitor

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock

/**
 * Process(pre) -> Add activity container
 * Process(start) -> add an activity to the container
 * Process(end) -> remove the activity from the container
 *
 * Container -> visibility bound to activity count (> 0)
 * */
object ActivityMonitorService {
	private val _containers = mutableMapOf<String, ActivityContainer>()
	val containers: Map<String, ActivityContainer>
		get() = _containers
	private val _nContainers = MutableStateFlow(0)
	val nContainers: StateFlow<Int> = _nContainers
	val lock = ReentrantLock()

	val scope = MainScope()

	val queueCount = MutableStateFlow(0)
	val runningCount = MutableStateFlow(0)
	val erroredCount = MutableStateFlow(0)
	val completedCount = MutableStateFlow(0)

	private fun updateQueueCount() {
		queueCount.value = _containers.values.sumOf { it.queueCount.value }
		runningCount.value = _containers.values.sumOf { it.runningCount.value }
		erroredCount.value = _containers.values.sumOf { it.erroredCount.value }
		completedCount.value = _containers.values.sumOf { it.completedCount.value }
	}

	operator fun get(id: String): ActivityContainer {
		return with(lock) {
			val container = _containers.getOrPut(id) {
				ActivityContainer().apply {
					scope.launch { queueCount.collect { updateQueueCount() } }
					scope.launch { runningCount.collect { updateQueueCount() } }
					scope.launch { erroredCount.collect { updateQueueCount() } }
					scope.launch { completedCount.collect { updateQueueCount() } }
				}
			}
			_nContainers.value = _containers.size
			container
		}
	}
	operator fun set(id: String, container: ActivityContainer) {
	    with(lock) {
	        _containers[id] = container
	    }
	}
	fun remove(id: String) {
		with(lock) {
			if (_containers.containsKey(id)) {
				_containers.remove(id)
				_nContainers.value = _containers.size
			}
		}
	}
}