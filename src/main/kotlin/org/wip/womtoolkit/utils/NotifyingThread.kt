package org.wip.womtoolkit.utils

class NotifyingThread(
	private val onFinish: () -> Unit,
	private val onError: (e: Exception) -> Unit,
	private val task: () -> Unit
) : Thread() {
	var errored: Exception? = null
		private set
	override fun run() {
		try {
			task()
		} catch (e: Exception) {
			onError(e)
		} finally {
			onFinish()
		}
	}
}