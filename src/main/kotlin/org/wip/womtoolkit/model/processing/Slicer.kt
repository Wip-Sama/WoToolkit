package org.wip.womtoolkit.model.processing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.wip.womtoolkit.model.ApplicationSettings
import org.wip.womtoolkit.model.Globals
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.concurrent.withLock


/**
 * UI (add folder/files) -> slicer (check folder and add ElementsToProcess to queue) -> UI (update ui and show new elements)
 * */
object Slicer {
	private var _queue: MutableStateFlow<MutableList<ElementToProcess>> = MutableStateFlow(mutableListOf())
	val queue: StateFlow<List<ElementToProcess>>
		get() = _queue.asStateFlow()

	private fun processElementParallel(element: ElementToProcess) {
		Thread { processElement(element) }.start()
	}

	private fun processElement(element: ElementToProcess) {
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

	fun canProcessElement(element: ElementToProcess): Boolean {
		if (element.elements.value.isEmpty()) return false
		if (element.outputFolder.value.isEmpty()) return false
		return true
	}

	// May not need to be public
	/**
	 * @throws IllegalStateException if the element at index is not ready for processing (missing output folder or elements)
	 * */
	fun processOne(index: Int = 0, parallelExecutionOnProcessingStart: Boolean = ApplicationSettings.slicerSettings.parallelExecution.value) {
		_queue.value.getOrNull(index)?.let {
			if (!canProcessElement(it)) {
				Globals.logger.info("Element at index $index is not ready for processing: ${it.elements.value}, output folder: ${it.outputFolder.value}")
				throw IllegalStateException("Element at index $index is not ready for processing")
			}

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

	private fun addElement(element: ElementToProcess) {
		_queue.value = _queue.value.toMutableList().apply { add(element) }
	}

	/**
	 * This function will add an element taking all the supported image formats from the input folder.
	 * @param inputFolder the folder containing the images to process
	 * @param outputFolder the folder where the processed images will be saved, defaults to inputFolder/${subFolderName} if saveInSubFolder is true WARNING: this will also default to inputFolder if saveInSubFolder is false and that could cause problems
	 * @param supportedFormats in the list of supported formats, defaults to Globals.IMAGE_INPUT_FORMATS
	 * @throws IllegalArgumentException if the element with the same inputFolder and elements already exists in the queue //could change this to a more specific throw
	 * @throws IllegalArgumentException if the inputFolder does not exist or is not a directory
	 * @throws IllegalArgumentException if the inputFolder was already added to the queue
	 * */
	fun addElementFromFolder(
		inputFolder: String,
		outputFolder: String = if (ApplicationSettings.slicerSettings.saveInSubFolder.value) {
			"${inputFolder}\\${ApplicationSettings.slicerSettings.subFolderName.value}"
		} else {
			inputFolder
		},
		supportedFormats: List<String> = Globals.IMAGE_INPUT_FORMATS
	) {
		val elements = mutableListOf<String>()
		val folderPath = Paths.get(inputFolder)

		if (!folderPath.toFile().exists())
			throw IllegalArgumentException("Folder $inputFolder does not exist")

		if (!folderPath.toFile().isDirectory)
			throw IllegalArgumentException("Path $inputFolder is not a directory")

		queue.value.any { element ->
			element.inputFolder.value == inputFolder
		}.let { exists ->
			if (exists) {
				throw IllegalArgumentException("Element with input folder $inputFolder already exists in the queue")
			}
		}

		folderPath.toFile().listFiles { file ->
			file.isFile && supportedFormats.any { ext -> file.extension.equals(ext, ignoreCase = true) }
		}?.forEach { file ->
			elements.add(file.name) // add only name and extension if inputFolder is present
		}

		addElement(ElementToProcess(
			inputFolder = inputFolder,
			elements = elements,
			outputFolder = outputFolder,
		))
	}

	/**
	 * This function will add an element taking all the files from the list and will ensure the files exist and are supported.
	 * @param files the list of files to add to the queue
	 * @param outputFolder the folder where the processed images will be saved, defaults to empty string (no output folder) Warning: if not provided, you won't be able to process the element
	 * @throws IllegalArgumentException if the file list is empty or if no valid files are provided
	 * */
	fun addElementFromFiles(
		files: List<String>,
		outputFolder: String = ""
	) {
		if (files.isNotEmpty()) {
			// Filter out non-existing files and directories
			val _files = files.filter { file ->
				val path = Paths.get(file)
				path.toFile().exists() && path.toFile().isFile && Globals.IMAGE_INPUT_FORMATS.any { ext ->
					path.toFile().extension.equals(ext, ignoreCase = true)
				}
			}

			if (_files.isEmpty()) {
				throw IllegalArgumentException("No valid files provided")
			}

			addElement(ElementToProcess(
				elements = _files.toMutableList()
			))
		}
	}

	fun removeElement(element: ElementToProcess) {
		_queue.value.remove(element)
		_queue.value = _queue.value.toMutableList() // trigger flow update
	}

	fun clearQueue() {
		_queue.value.clear()
		_queue.value = _queue.value.toMutableList() // trigger flow update
	}
}