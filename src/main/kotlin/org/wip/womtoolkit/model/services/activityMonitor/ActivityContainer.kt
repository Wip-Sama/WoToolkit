package org.wip.womtoolkit.model.services.activityMonitor

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.wip.womtoolkit.model.Globals
import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.enums.ThreadMode
import org.wip.womtoolkit.model.services.notification.NotificationData
import org.wip.womtoolkit.model.services.notification.NotificationService
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock

/**
 * */
class ActivityContainer {
	private val threadPool = object : ThreadPoolExecutor(
		4, 4, 0L, TimeUnit.SECONDS,
		LinkedBlockingQueue(),
		ThreadFactory { r ->
			Thread(r).apply { isDaemon = true }
		}
	) {
		override fun submit(task: Runnable): Future<*> {
			val future = super.submit(task)
			with(lock) {
				queueCount.value++
			}
			return future
		}

		override fun beforeExecute(t: Thread?, r: Runnable?) {
			super.beforeExecute(t, r)
			with(lock) {
				runningCount.value = activeCount
				queueCount.value = queue.size
			}
		}
		override fun afterExecute(r: Runnable, t: Throwable?) {
			super.afterExecute(r, t)
			if (t == null) {
				Globals.logger.info("A slice has been terminated successfully")
				NotificationService.addNotification(NotificationData(
					localizedContent = "success.elementProcessed",
					type = NotificationTypes.SUCCESS
				))
				with(lock) {
					queueCount.value = queue.size
					runningCount.value = if (activeCount == 1 && queueCount.value == 0) 0 else runningCount.value-1
					completedCount.value++
				}
			} else {
				Globals.logger.warning("Something went wrong: ${t.message}")
				NotificationService.addNotification(NotificationData(
					localizedContent = "error.generic",
					type = NotificationTypes.ERROR,
				))
				with(lock) {
					queueCount.value = queue.size
					runningCount.value = if (activeCount == 1 && queueCount.value == 0) 0 else runningCount.value-1
					erroredCount.value++
				}
			}
		}
	}

	private val scope = MainScope()
	private val lock = ReentrantLock()

	val queueCount = MutableStateFlow(0)
	val runningCount = MutableStateFlow(0)
	val erroredCount = MutableStateFlow(0)
	val completedCount = MutableStateFlow(0)
	var threadMode = ThreadMode.MULTI_THREAD
		set(value) {
			field = value
			when (value) {
				ThreadMode.SINGLE_THREAD -> setSingleThreadMode()
				ThreadMode.MULTI_THREAD -> setMultiThreadMode()
			}
		}

	init {
		scope.launch {
			queueCount.collect {
				Globals.logger.info("Queue count: $it")
			}
		}
		scope.launch {
			runningCount.collect {
				Globals.logger.info("Running count: $it")
			}
		}
		scope.launch {
			erroredCount.collect {
				Globals.logger.info("Errored count: $it")
			}
		}
		scope.launch {
			completedCount.collect {
				Globals.logger.info("Completed count: $it")
			}
		}
	}

	private fun setSingleThreadMode() {
		with(lock) {
			threadPool.corePoolSize = 1
			threadPool.maximumPoolSize = 1
		}
	}

	private fun setMultiThreadMode() {
		with(lock) {
			threadPool.maximumPoolSize = 4
			threadPool.corePoolSize = 4
		}
	}

	fun submit(task: () -> Unit): Future<*> {
		return threadPool.submit(task)
	}

	fun submit(task: Runnable): Future<*> {
		return threadPool.submit(task)
	}
}