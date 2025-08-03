package org.wip.womtoolkit.model

import org.wip.womtoolkit.model.enums.NotificationTypes
import org.wip.womtoolkit.model.enums.Platforms
import org.wip.womtoolkit.model.services.notification.NotificationData
import org.wip.womtoolkit.model.services.notification.NotificationService
import org.wip.womtoolkit.utils.Version
import java.lang.management.ManagementFactory
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object Globals {
	const val LOCALES_PATH: String = "/locales/"

	const val MAX_SUBFOLDER_NAME_LENGTH = 260
	val FOLDER_REGEX = "^(?![ .]$)[^<>:\"/\\\\|?*]+(?<![ .])$".toRegex()

	val IMAGE_INPUT_FORMATS = listOf("png", "jpg", "jpeg", "webp", "gif", "bmp", "tiff", "psd")
	val IMAGE_OUTPUT_FORMATS = listOf("png", "jpg", "webp")

	val ARCHIVE_INPUT_FORMATS = listOf("zip")
	val ARCHIVE_OUTPUT_FORMATS = listOf("zip")

	val TOOLKIT_VERSION: Version = Version(0, 0, 0, 1) // epoch.major.minor.patch

	val isDebug: Boolean by lazy {
		ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}

	val logger: Logger = Logger.getLogger(javaClass.getName())

	val PLATFORM = when (System.getProperty("os.name").lowercase()) {
		"linux" -> Platforms.LINUX
		"mac os x" -> Platforms.MACOS
		"windows" -> Platforms.WINDOWS
		"windows 11" -> Platforms.WINDOWS
		else -> {
			val osName = System.getProperty("os.name").lowercase()
			logger.warning("Unsupported platform: $osName")
			NotificationService.addNotification(NotificationData(
				localizedContent = "warning.unsupportedPlatform",
				type = NotificationTypes.WARNING,
			))
			Platforms.UNKNOWN
		}
	}

	init {
		logger.level = Level.ALL
		try {
			val logDir = java.io.File("Log")
			if (!logDir.exists()) logDir.mkdirs()
			// Trova il numero di apertura (file già presenti oggi)
			val date = java.time.LocalDate.now().toString()
			val existing = logDir.listFiles { f -> f.name.startsWith(date) && f.name.endsWith(".log") } ?: emptyArray()
			val nApertura = existing.size + 1
			val logFileName = "Log/${date}-$nApertura.log"
			val fileHandler = FileHandler(logFileName, true)
			fileHandler.level = Level.ALL
			fileHandler.formatter = SimpleFormatter()
			logger.addHandler(fileHandler)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}