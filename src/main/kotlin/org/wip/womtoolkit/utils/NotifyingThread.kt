package org.wip.womtoolkit.utils

class NotifyingThread(
	private val onFinish: () -> Unit,
	private val onError: (e: Exception) -> Unit,
	private val task: Runnable
) : Thread() {
	constructor(
		onFinish: () -> Unit,
		onError: (e: Exception) -> Unit,
		task: () -> Unit
	) : this(onFinish, onError, Runnable { task() })

	var errored: Exception? = null
		private set
	override fun run() {
		try {
			task.run()
		} catch (e: Exception) {
			onError(e)
		} finally {
			onFinish()
		}
	}
}