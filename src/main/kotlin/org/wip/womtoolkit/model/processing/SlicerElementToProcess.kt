package org.wip.womtoolkit.model.processing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.withLock

class SlicerElementToProcess: ElementsToProcess {
	private val _inputFolder: MutableStateFlow<String>

	val inputFolder: StateFlow<String>
		get() = _inputFolder.asStateFlow()

	constructor(inputFolder: String, elements: MutableList<String>, outputFolder: String, progress: Double) : super(
		elements,
		outputFolder,
		progress
	) {
		this._inputFolder = MutableStateFlow(inputFolder)
	}

	fun setInputFolder(inputFolder: String) {
		lock.withLock {
			if (inputFolder.isBlank()) {
				throw IllegalArgumentException("Input folder cannot be blank")
			}
			_inputFolder.value = inputFolder
		}
	}
}
