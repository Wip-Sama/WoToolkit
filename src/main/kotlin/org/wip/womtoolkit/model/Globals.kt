package org.wip.womtoolkit.model

import java.lang.management.ManagementFactory
import java.util.logging.Level
import java.util.logging.Logger

object Globals {
	const val LOCALES_PATH: String = "/locales/"

	const val MAX_SUBFOLDER_NAME_LENGTH = 260
	val FOLDER_REGEX = "^(?![ .]$)[^<>:\"/\\\\|?*]+(?<![ .])$".toRegex()

	val IMAGE_INPUT_FORMATS = listOf("png", "jpg", "jpeg", "webp", "gif", "bmp", "tiff", "psd")
	val IMAGE_OUTPUT_FORMATS = listOf("png", "jpg", "webp")

	val ARCHIVE_INPUT_FORMATS = listOf("zip")
	val ARCHIVE_OUTPUT_FORMATS = listOf("zip")

	val isDebug: Boolean by lazy {
		ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}

	val logger: Logger? = Logger.getLogger(javaClass.getName())

	init {
		logger?.level = Level.WARNING
	}
}