package org.wip.womtoolkit.model.processing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wip.womtoolkit.model.ApplicationSettings
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.concurrent.withLock


/**
 * UI (add folder/files) -> slicer (check folder and add ElementsToProcess to queue) -> UI (update ui and show new elements)
 * */
object Slicer {
	private var _queue: MutableStateFlow<MutableList<SlicerElementToProcess>> = MutableStateFlow(mutableListOf())
	val queue: StateFlow<List<SlicerElementToProcess>>
		get() = _queue.asStateFlow()

	private fun processElementParallel(element: SlicerElementToProcess) {
		Thread { processElement(element) }.start()
	}

	private fun processElement(element: SlicerElementToProcess) {
		with(element) {
			lock.withLock {
				val channels = mutableListOf<FileChannel>()
				val locks = mutableListOf<FileLock>()

				elements.value.forEach { file->
					val channel = FileChannel.open(Paths.get(file), StandardOpenOption.READ)
					val lock = channel.lock()
					channels.add(channel)
					locks.add(lock)
				}

				// ora che siamo protetti da stronzi che vogliono cancellarci i file possiamo processarli
				// qui la call alle funzioni di slicing o al modulo python


				locks.forEach { it.release() }
				channels.forEach { it.close() }
			}
		}
	}

	fun processOne(index: Int = 0, parallelExecutionOnProcessingStart: Boolean = ApplicationSettings.slicerSettings.parallelExecution.value) {
		_queue.value.getOrNull(index)?.let {
			if (parallelExecutionOnProcessingStart)
				processElementParallel(it)
			else
				processElement(it)
		}
	}

	fun processSome(indexes: List<Int>) {
		val parallelExecutionOnProcessingStart = ApplicationSettings.slicerSettings.parallelExecution.value
		for (index in indexes) {
			processOne(index, parallelExecutionOnProcessingStart)
		}
	}

	fun processAll() {
		val parallelExecutionOnProcessingStart = ApplicationSettings.slicerSettings.parallelExecution.value
		for (index in _queue.value.indices) {
			processOne(index, parallelExecutionOnProcessingStart)
		}
	}
}