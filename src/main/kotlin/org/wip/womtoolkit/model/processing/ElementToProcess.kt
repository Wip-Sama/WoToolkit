package org.wip.womtoolkit.model.processing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// Warning: this class does not ensure the lack of duplicates in the element list.
// Should be Thread-safe.
open class ElementToProcess{
	val lock: Lock = ReentrantLock()

	private val _elements: MutableStateFlow<MutableList<String>>
	private val _outputFolder: MutableStateFlow<String>
	private val _progress: MutableStateFlow<Double>
	private val _inputFolder: MutableStateFlow<String>
	private val _processing : MutableStateFlow<Boolean> = MutableStateFlow(false)

	val elements: StateFlow<List<String>>
		get() = _elements.asStateFlow()
	val outputFolder: StateFlow<String>
		get() = _outputFolder.asStateFlow()
	val progress: StateFlow<Double>
		get() = _progress.asStateFlow()
	val inputFolder: StateFlow<String>
		get() = _inputFolder.asStateFlow()
	val processing: StateFlow<Boolean>
		get() = _processing.asStateFlow()

	constructor(
		elements: List<String>,
		outputFolder: String = "",
		progress: Double = 0.0,
		inputFolder: String = ""
	) {
		this._elements = MutableStateFlow(elements.toMutableList())
		this._outputFolder = MutableStateFlow(outputFolder)
		this._progress = MutableStateFlow(progress)
		this._inputFolder = MutableStateFlow(inputFolder)
	}

	/** Swap the element at index e1 with the element at index e2.
	 * @param e1 index of the first element to swap
	 * @param e2 index of the second element to swap
	 * @throws IndexOutOfBoundsException if e1 or e2 are out of bounds of the element list
	 * */
	fun swapElementPosition(e1: Int, e2: Int) {
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

	fun moveElementToPosition(element: Int, position: Int) {
		lock.withLock {
			if (position in _elements.value.indices) {
				val e = _elements.value.removeAt(element)
				_elements.value.add(position, e)
			} else {
				throw IndexOutOfBoundsException("Position out of bounds: $position, size=${_elements.value.size}")
			}
		}
	}

	//TODO: Validation for each value changed

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

	fun setInputFolder(inputFolder: String) {
		lock.withLock {
			_inputFolder.value = inputFolder
		}
	}

	fun setProcessing(processing: Boolean) {
		lock.withLock {
			_processing.value = processing
		}
	}
}