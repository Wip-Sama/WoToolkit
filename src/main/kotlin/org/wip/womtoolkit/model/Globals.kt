package org.wip.womtoolkit.model

import java.lang.management.ManagementFactory
import java.util.logging.Level
import java.util.logging.Logger

object Globals {
	const val LOCALES_PATH: String = "/locales/"

	val isDebug: Boolean by lazy {
		ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}

	val logger: Logger? = Logger.getLogger(javaClass.getName())

	init {
		logger?.level = Level.WARNING
	}
}