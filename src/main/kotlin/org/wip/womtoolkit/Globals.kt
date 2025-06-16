package org.wip.womtoolkit

import java.util.logging.Logger

object Globals {
	const val LOCALES_PATH: String = "/locales/"
	val logger: Logger? = Logger.getLogger(WomToolkit::class.java.getName())
	val isDebug: Boolean by lazy {
		java.lang.management.ManagementFactory.getRuntimeMXBean()
			.inputArguments.any { it.contains("-agentlib:jdwp") }
	}
}