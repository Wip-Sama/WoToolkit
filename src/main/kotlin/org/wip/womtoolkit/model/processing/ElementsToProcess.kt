package org.wip.womtoolkit.model.processing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// Warning: this class does not ensure the lack of duplicates in the element list.
// Should be Thread-safe.
open class ElementsToProcess{
	val lock: Lock = ReentrantLock()

	private val _elements: MutableStateFlow<MutableList<String>>
	private val _outputFolder: MutableStateFlow<String>
	private val _progress: MutableStateFlow<Double>

	val elements: StateFlow<List<String>>
		get() = _elements.asStateFlow()
	val outputFolder: StateFlow<String>
		get() = _outputFolder.asStateFlow()
	val progress: StateFlow<Double>
		get() = _progress.asStateFlow()

	constructor(
		elements: List<String>,
		outputFolder: String,
		progress: Double,
	) {
		this._elements = MutableStateFlow(elements.toMutableList())
		this._outputFolder = MutableStateFlow(outputFolder)
		this._progress = MutableStateFlow(progress)
	}

	/** Change the element at index e1 with the element at index e2.
	 * @param e1 index of the first element to swap
	 * @param e2 index of the second element to swap
	 * @throws IndexOutOfBoundsException if e1 or e2 are out of bounds of the element list
	 * */
	fun changeElementPosition(e1: Int, e2: Int) {
		lock.withLock {
			if (e1 in _elements.value.indices && e2 in _elements.value.indices) {
				val temp = _elements.value[e1]
				_elements.value[e1] = _elements.value[e2]
				_elements.value[e2] = temp
			} else {
				// It's not really necessary, but I wanted to have a throw here for some reason
				throw IndexOutOfBoundsException("Index out of bounds: e1=$e1, e2=$e2, size=${_elements.value.size}")
			}
		}
	}

	fun setElements(elements: MutableList<String>) {
		lock.withLock {
			_elements.value = elements
		}
	}

	fun setOutputFolder(outputFolder: String) {
		lock.withLock {
			_outputFolder.value = outputFolder
		}
	}

	fun setProgress(progress: Double) {
		lock.withLock {
			_progress.value = progress
		}
	}
}